package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

// Erro da API já com a mensagem para exibir. `fields` traz erros por campo do formulário.
class ApiException(
    val code: String,
    override val message: String,
    val fields: Map<String, String> = emptyMap(),
) : Exception(message)

private const val NetworkError = "NETWORK_ERROR"
private val SessionEndedCodes = setOf("INVALID_REFRESH_TOKEN", "SESSION_EXPIRED", "INVALID_TOKEN", "UNAUTHORIZED")

class RangoApi(private val baseUrl: String = ApiBaseUrl) {
    private val json = Json { ignoreUnknownKeys = true }
    private val client = HttpClient {
        expectSuccess = false
        install(ContentNegotiation) { json(json) }
    }

    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var keepSignedIn = false
    // Só uma renovação por vez: reutilizar um refresh token antigo encerra todas as sessões.
    private val refreshMutex = Mutex()

    // Chamado quando a sessão não pode mais ser renovada (o app volta para o login).
    var onSessionExpired: () -> Unit = {}

    // Sessão

    suspend fun login(email: String, password: String, keepSignedIn: Boolean): UserDto {
        val session: SessionDto = call(HttpMethod.Post, "/auth/login", auth = false) {
            jsonBody(LoginRequest(email.trim(), password, keepSignedIn))
        }
        startSession(session, keepSignedIn)
        return session.user
    }

    suspend fun register(request: RegisterRequest): UserDto {
        val session: SessionDto = call(HttpMethod.Post, "/auth/register", auth = false) { jsonBody(request) }
        startSession(session, request.keepSignedIn)
        return session.user
    }

    // Abertura do app: se houver refresh token salvo, renova a sessão.
    suspend fun restoreSession(): Boolean {
        refreshToken = SessionStorage.readRefreshToken() ?: return false
        keepSignedIn = true
        return try {
            refreshSession(expiredToken = null)
            true
        } catch (e: ApiException) {
            false
        }
    }

    // Catálogo

    suspend fun home(): HomeDto = call(HttpMethod.Get, "/home")

    suspend fun products(categorySlug: String? = null): List<ProductDto> =
        call<ListResponse<ProductDto>>(HttpMethod.Get, "/products") {
            if (categorySlug != null) parameter("category", categorySlug)
            parameter("pageSize", 50)
        }.data

    suspend fun product(id: String): ProductDto = call(HttpMethod.Get, "/products/$id")

    suspend fun setFavorite(productId: String, favorite: Boolean) {
        call<Unit>(if (favorite) HttpMethod.Put else HttpMethod.Delete, "/favorites/$productId")
    }

    // Carrinho

    suspend fun cart(): CartDto = call(HttpMethod.Get, "/cart")

    suspend fun addToCart(productId: String, quantity: Int, addonIds: Set<String>, notes: String): CartDto =
        call(HttpMethod.Post, "/cart/items") { jsonBody(AddCartItemRequest(productId, quantity, addonIds.toList(), notes)) }

    suspend fun setQuantity(lineId: Int, quantity: Int): CartDto =
        call(HttpMethod.Patch, "/cart/items/$lineId") { jsonBody(QuantityRequest(quantity)) }

    suspend fun removeLine(lineId: Int): CartDto = call(HttpMethod.Delete, "/cart/items/$lineId")

    suspend fun clearCart() {
        call<Unit>(HttpMethod.Delete, "/cart")
    }

    suspend fun applyCoupon(code: String): CartDto =
        call(HttpMethod.Put, "/cart/coupon") { jsonBody(CouponRequest(code.trim())) }

    suspend fun removeCoupon(): CartDto = call(HttpMethod.Delete, "/cart/coupon")

    // Endereços e pagamento

    suspend fun addresses(): List<AddressDto> = call<ListResponse<AddressDto>>(HttpMethod.Get, "/addresses").data

    suspend fun addAddress(request: NewAddressRequest): AddressDto =
        call(HttpMethod.Post, "/addresses") { jsonBody(request) }

    suspend fun selectAddress(id: Int) {
        call<Unit>(HttpMethod.Post, "/addresses/$id/select")
    }

    suspend fun paymentMethods(): List<PaymentMethodDto> =
        call<ListResponse<PaymentMethodDto>>(HttpMethod.Get, "/payment-methods").data

    // Pedidos

    suspend fun createOrder(paymentMethodId: Int): OrderDto =
        call(HttpMethod.Post, "/orders") { jsonBody(CreateOrderRequest(paymentMethodId)) }

    suspend fun order(id: Int): OrderDto = call(HttpMethod.Get, "/orders/$id")

    // group: "live" (em andamento) ou "past" (entregues e cancelados).
    suspend fun orders(group: String): List<OrderListItemDto> =
        call<ListResponse<OrderListItemDto>>(HttpMethod.Get, "/orders") {
            parameter("group", group)
            parameter("pageSize", 50)
        }.data

    // Troca o carrinho pelos itens do pedido.
    suspend fun reorder(id: Int): CartDto = call(HttpMethod.Post, "/orders/$id/reorder")

    // Infra

    private suspend inline fun <reified T> call(
        method: HttpMethod,
        path: String,
        auth: Boolean = true,
        noinline block: HttpRequestBuilder.() -> Unit = {},
    ): T {
        val response = send(method, path, auth, block)
        return if (T::class == Unit::class) Unit as T else response.body()
    }

    // Envia a requisição; se o access token venceu, renova a sessão e tenta de novo uma vez.
    private suspend fun send(
        method: HttpMethod,
        path: String,
        auth: Boolean,
        block: HttpRequestBuilder.() -> Unit,
    ): HttpResponse {
        val usedToken = accessToken
        val response = rawSend(method, path, auth, block)
        if (response.status.isSuccess()) return response

        val error = readError(response)
        if (auth && response.status == HttpStatusCode.Unauthorized && error.code == "TOKEN_EXPIRED") {
            refreshSession(usedToken)
            val retry = rawSend(method, path, auth, block)
            if (retry.status.isSuccess()) return retry
            throw readError(retry)
        }
        if (auth && error.code in SessionEndedCodes) endSession()
        throw error
    }

    private suspend fun rawSend(
        method: HttpMethod,
        path: String,
        auth: Boolean,
        block: HttpRequestBuilder.() -> Unit,
    ): HttpResponse = try {
        client.request(baseUrl + path) {
            this.method = method
            if (auth) accessToken?.let { bearerAuth(it) }
            block()
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        throw ApiException(NetworkError, "Não foi possível conectar ao servidor. Tente novamente.")
    }

    private suspend fun refreshSession(expiredToken: String?) = refreshMutex.withLock {
        // Outra requisição já renovou enquanto esta esperava.
        if (accessToken != expiredToken) return@withLock
        val token = refreshToken ?: run {
            endSession()
            throw ApiException("SESSION_EXPIRED", "Sua sessão expirou. Entre novamente.")
        }
        val response = rawSend(HttpMethod.Post, "/auth/refresh", auth = false) { jsonBody(RefreshRequest(token)) }
        if (!response.status.isSuccess()) {
            val error = readError(response)
            if (error.code != NetworkError) endSession()
            throw error
        }
        startSession(response.body(), keepSignedIn)
    }

    private fun startSession(session: SessionDto, keep: Boolean) {
        accessToken = session.accessToken
        refreshToken = session.refreshToken
        keepSignedIn = keep
        // O refresh token é rotacionado a cada renovação: sempre salva o novo.
        SessionStorage.saveRefreshToken(if (keep) session.refreshToken else null)
    }

    private fun endSession() {
        val hadSession = accessToken != null || refreshToken != null
        accessToken = null
        refreshToken = null
        SessionStorage.saveRefreshToken(null)
        if (hadSession) onSessionExpired()
    }

    private suspend fun readError(response: HttpResponse): ApiException = try {
        val error = response.body<ErrorBody>().error
        ApiException(error.code, error.message, error.details.associate { it.field to it.message })
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiException("HTTP_${response.status.value}", "Algo deu errado. Tente novamente.")
    }
}

private inline fun <reified T> HttpRequestBuilder.jsonBody(value: T) {
    contentType(ContentType.Application.Json)
    setBody(value)
}
