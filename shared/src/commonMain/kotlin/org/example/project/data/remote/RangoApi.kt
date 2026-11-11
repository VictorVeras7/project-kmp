package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Erro da API já com a mensagem para exibir. `fields` traz erros por campo do formulário.
class ApiException(
    val code: String,
    override val message: String,
    val fields: Map<String, String> = emptyMap(),
) : Exception(message)

class RangoApi(private val baseUrl: String = ApiBaseUrl) {
    private val json = Json { ignoreUnknownKeys = true }
    private val client = HttpClient {
        expectSuccess = false
        install(ContentNegotiation) { json(json) }
    }

    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var keepSignedIn = false

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
}

private inline fun <reified T> HttpRequestBuilder.jsonBody(value: T) {
    contentType(ContentType.Application.Json)
    setBody(value)
}
