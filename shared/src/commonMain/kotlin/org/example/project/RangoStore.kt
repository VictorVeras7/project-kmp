package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.example.project.data.AddressForm
import org.example.project.data.Cart
import org.example.project.data.DeliveryAddress
import org.example.project.data.Order
import org.example.project.data.OrderDetail
import org.example.project.data.OrderSummary
import org.example.project.data.PaymentOption
import org.example.project.data.Product
import org.example.project.data.SampleAddons
import org.example.project.data.remote.ApiException
import org.example.project.data.remote.NewAddressRequest
import org.example.project.data.remote.ProductDto
import org.example.project.data.remote.RangoApi
import org.example.project.data.remote.RegisterRequest
import org.example.project.data.remote.toAddon
import org.example.project.data.remote.toAddress
import org.example.project.data.remote.toCart
import org.example.project.data.remote.toDetail
import org.example.project.data.remote.toOption
import org.example.project.data.remote.toOrder
import org.example.project.data.remote.toProduct
import org.example.project.data.remote.toSummary
import org.example.project.ui.signup.SignupData

private val CouponErrors = setOf("INVALID_COUPON", "COUPON_MIN_SUBTOTAL")

// Estado do app vindo do backend e as ações que conversam com ele.
// As telas continuam recebendo modelos simples; aqui ficam as chamadas e o tratamento de erro.
class RangoStore(private val api: RangoApi, private val scope: CoroutineScope) {
    var userName by mutableStateOf("")
        private set
    var products by mutableStateOf(listOf<Product>())
        private set
    var favoriteIds by mutableStateOf(setOf<String>())
        private set
    var addons by mutableStateOf(SampleAddons)
        private set
    var cart by mutableStateOf(Cart())
        private set
    var couponInput by mutableStateOf("")
        private set
    var addresses by mutableStateOf(listOf<DeliveryAddress>())
        private set
    var selectedAddressId by mutableStateOf<Int?>(null)
        private set
    var payments by mutableStateOf(listOf<PaymentOption>())
        private set
    var payment by mutableStateOf<PaymentOption?>(null)
    var order by mutableStateOf<Order?>(null)
        private set

    // Meus pedidos: listas das duas abas e o pedido aberto nos detalhes.
    var liveOrders by mutableStateOf(listOf<OrderSummary>())
        private set
    var pastOrders by mutableStateOf(listOf<OrderSummary>())
        private set
    var ordersLoading by mutableStateOf(false)
        private set
    var orderDetail by mutableStateOf<OrderDetail?>(null)
        private set

    // Mensagem de erro para o aviso na parte de baixo da tela; o App limpa depois de mostrar.
    var message by mutableStateOf<String?>(null)

    private var categorySlugs = mapOf<String, String>()

    val selectedAddress: DeliveryAddress?
        get() = addresses.firstOrNull { it.id == selectedAddressId } ?: addresses.firstOrNull()

    // Sessão

    suspend fun login(email: String, password: String, keepSignedIn: Boolean): Map<String, String> =
        authenticate { api.login(email, password, keepSignedIn) }

    suspend fun register(data: SignupData): Map<String, String> = authenticate {
        api.register(
            RegisterRequest(
                name = data.name,
                email = data.email,
                phone = data.phone,
                password = data.password,
                passwordConfirmation = data.confirmPassword,
                acceptTerms = true,
                keepSignedIn = true,
            ),
        )
    }

    // Devolve os erros por campo; erros sem campo viram aviso e uma chave vazia (falha sem campo).
    private suspend fun authenticate(block: suspend () -> Unit): Map<String, String> = try {
        block()
        loadAll()
        emptyMap()
    } catch (e: ApiException) {
        when {
            e.code == "INVALID_CREDENTIALS" -> mapOf("password" to e.message)
            e.code == "EMAIL_IN_USE" -> mapOf("email" to e.message)
            e.fields.isNotEmpty() -> e.fields
            else -> {
                message = e.message
                mapOf("" to e.message)
            }
        }
    }

    suspend fun restoreSession(): Boolean {
        if (!api.restoreSession()) return false
        loadAll()
        return true
    }

    fun onSessionExpired(action: () -> Unit) {
        api.onSessionExpired = {
            clear()
            message = "Sua sessão expirou. Entre novamente."
            action()
        }
    }

    private fun clear() {
        userName = ""
        products = emptyList()
        favoriteIds = emptySet()
        cart = Cart()
        couponInput = ""
        addresses = emptyList()
        selectedAddressId = null
        payments = emptyList()
        payment = null
        order = null
        liveOrders = emptyList()
        pastOrders = emptyList()
        orderDetail = null
    }

    // Carrega tudo o que a Home e o checkout precisam, em paralelo.
    private suspend fun loadAll() {
        // coroutineScope: se uma chamada falhar, as outras são canceladas e o erro chega ao safely.
        safely<Unit> {
            coroutineScope {
                val home = async { api.home() }
                val cartDto = async { api.cart() }
                val addressList = async { api.addresses() }
                val paymentList = async { api.paymentMethods() }
                // Pedidos em andamento: badge da aba "Pedidos".
                val live = async { api.orders("live") }

                val homeDto = home.await()
                userName = homeDto.user.name
                categorySlugs = homeDto.categories.associate { it.name to it.slug }
                mergeProducts(homeDto.sections.flatMap { it.products })

                addresses = addressList.await().map { it.toAddress() }
                selectedAddressId = addressList.await().firstOrNull { it.isSelected }?.id ?: homeDto.selectedAddress?.id
                payments = paymentList.await().mapNotNull { it.toOption() }
                payment = payment?.let { current -> payments.firstOrNull { it.id == current.id } } ?: payments.firstOrNull()
                setCart(cartDto.await().toCart())
                liveOrders = live.await().map { it.toSummary() }
            }
        }
    }

    // Catálogo

    // Chips da Home: busca os produtos da categoria ("Todos" já vem da Home).
    fun loadCategory(name: String) {
        val slug = categorySlugs[name] ?: return
        launchSafely { mergeProducts(api.products(slug)) }
    }

    // Detalhe: ingredientes e adicionais só vêm em GET /products/:id.
    fun loadProduct(id: String) {
        launchSafely {
            val dto = api.product(id)
            mergeProducts(listOf(dto))
            dto.addons?.let { list -> addons = list.map { it.toAddon() } }
        }
    }

    fun toggleFavorite(id: String) {
        val favorite = id !in favoriteIds
        // Atualiza na hora e desfaz se o servidor recusar.
        favoriteIds = if (favorite) favoriteIds + id else favoriteIds - id
        scope.launch {
            try {
                api.setFavorite(id, favorite)
            } catch (e: ApiException) {
                favoriteIds = if (favorite) favoriteIds - id else favoriteIds + id
                message = e.message
            }
        }
    }

    private fun mergeProducts(list: List<ProductDto>) {
        val byId = products.associateBy { it.id }.toMutableMap()
        list.forEach { dto ->
            val product = dto.toProduct()
            val old = byId[dto.id]
            // A listagem não traz ingredientes: mantém os que vieram do detalhe.
            byId[dto.id] = if (product.ingredients.isEmpty() && old != null) product.copy(ingredients = old.ingredients) else product
        }
        products = byId.values.toList()
        val favorites = list.filter { it.isFavorite }.map { it.id }.toSet()
        val notFavorites = list.filterNot { it.isFavorite }.map { it.id }.toSet()
        favoriteIds = favoriteIds - notFavorites + favorites
    }

    // Carrinho

    fun addToCart(productId: String, quantity: Int, addonIds: Set<String> = emptySet(), notes: String = "") {
        launchSafely { setCart(api.addToCart(productId, quantity, addonIds, notes).toCart()) }
    }

    fun changeQuantity(key: String, delta: Int) {
        val line = cart.items.firstOrNull { it.key == key } ?: return
        val quantity = line.quantity + delta
        launchSafely {
            val updated = if (quantity <= 0) api.removeLine(line.lineId) else api.setQuantity(line.lineId, quantity)
            setCart(updated.toCart())
        }
    }

    fun removeLine(key: String) {
        val line = cart.items.firstOrNull { it.key == key } ?: return
        launchSafely { setCart(api.removeLine(line.lineId).toCart()) }
    }

    fun clearCart() {
        launchSafely {
            api.clearCart()
            setCart(Cart())
            couponInput = ""
        }
    }

    // Editar o código depois de aplicado tira o cupom, como no protótipo.
    fun changeCoupon(text: String) {
        couponInput = text
        if (cart.couponApplied) launchSafely { setCart(api.removeCoupon().toCart()) }
    }

    suspend fun applyCoupon() {
        try {
            setCart(api.applyCoupon(couponInput).toCart())
        } catch (e: ApiException) {
            // Cupom recusado aparece no próprio campo ("Cupom inválido."); outros erros viram aviso.
            if (e.code !in CouponErrors) message = e.message
        }
    }

    private suspend fun setCart(value: Cart) {
        cart = value
        value.couponCode?.let { couponInput = it }
        // Linhas de produtos que ainda não estão no catálogo local (ex.: carrinho de outra sessão).
        val missing = value.items.map { it.productId }.filter { id -> products.none { it.id == id } }.distinct()
        if (missing.isNotEmpty()) mergeProducts(missing.map { api.product(it) })
    }

    // Endereços e pagamento

    fun selectAddress(index: Int) {
        val address = addresses.getOrNull(index) ?: return
        val previous = selectedAddressId
        selectedAddressId = address.id
        scope.launch {
            try {
                api.selectAddress(address.id)
            } catch (e: ApiException) {
                selectedAddressId = previous
                message = e.message
            }
        }
    }

    suspend fun saveAddress(form: AddressForm): Boolean = safely {
        val saved = api.addAddress(
            NewAddressRequest(
                label = "Novo endereço",
                cep = form.cep,
                street = form.street,
                complement = form.complement,
                neighborhood = form.neighborhood,
                city = form.city,
                select = true,
            ),
        ).toAddress()
        addresses = addresses + saved
        selectedAddressId = saved.id
    } != null

    // Pedidos

    suspend fun confirmOrder(): Boolean {
        val option = payment ?: return false
        return safely {
            order = api.createOrder(option.id).toOrder()
            // O servidor esvazia o carrinho ao criar o pedido.
            cart = Cart()
            couponInput = ""
            loadOrders()
        } != null
    }

    // Recarrega as duas abas de "Meus pedidos".
    fun loadOrders() {
        ordersLoading = true
        scope.launch {
            safely {
                coroutineScope {
                    val live = async { api.orders("live") }
                    val past = async { api.orders("past") }
                    liveOrders = live.await().map { it.toSummary() }
                    pastOrders = past.await().map { it.toSummary() }
                }
            }
            ordersLoading = false
        }
    }

    fun loadOrderDetail(id: Int) {
        if (orderDetail?.id != id) orderDetail = null
        launchSafely { orderDetail = api.order(id).toDetail() }
    }

    // Abre o acompanhamento de um pedido do histórico.
    suspend fun trackOrder(id: Int): Boolean = safely { order = api.order(id).toOrder() } != null

    // "Pedir novamente": o servidor troca o carrinho pelos itens do pedido.
    suspend fun reorder(id: Int): Boolean = safely {
        setCart(api.reorder(id).toCart())
        couponInput = ""
        message = "Itens adicionados ao carrinho."
    } != null

    // Consulta o pedido atual; usado em polling enquanto ele está em andamento.
    suspend fun refreshOrder() {
        val current = order ?: return
        safely { order = api.order(current.id).toOrder() }
    }

    // Infra

    private suspend fun <T> safely(block: suspend () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiException) {
        message = e.message
        null
    } catch (e: Exception) {
        // Resposta fora do formato esperado: não derruba o app.
        message = "Algo deu errado. Tente novamente."
        null
    }

    private fun launchSafely(block: suspend () -> Unit) {
        scope.launch { safely(block) }
    }
}
