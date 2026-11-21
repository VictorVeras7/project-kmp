package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.example.project.data.AddressForm
import org.example.project.data.AnyDeliveryTime
import org.example.project.data.Cart
import org.example.project.data.ProductCategories
import org.example.project.data.SearchFilters
import org.example.project.data.SearchMaxPrice
import org.example.project.data.SearchSuggestion
import org.example.project.data.formatMoney
import org.example.project.data.formatRating
import org.example.project.data.DeliveryAddress
import org.example.project.data.NotificationPrefs
import org.example.project.data.Order
import org.example.project.data.OrderDetail
import org.example.project.data.OrderSummary
import org.example.project.data.PaymentOption
import org.example.project.data.Product
import org.example.project.data.SampleAddons
import org.example.project.data.ThemeMode
import org.example.project.data.UserProfile
import org.example.project.data.remote.ApiException
import org.example.project.data.remote.NewAddressRequest
import org.example.project.data.remote.ProductDto
import org.example.project.data.remote.RangoApi
import org.example.project.data.remote.RegisterRequest
import org.example.project.data.remote.SettingsDto
import org.example.project.data.remote.UpdateProfileRequest
import org.example.project.data.remote.UpdateSettingsRequest
import org.example.project.data.remote.toAddon
import org.example.project.data.remote.toAddress
import org.example.project.data.remote.toCart
import org.example.project.data.remote.toDetail
import org.example.project.data.remote.toDto
import org.example.project.data.remote.toOption
import org.example.project.data.remote.toOrder
import org.example.project.data.remote.toPrefs
import org.example.project.data.remote.toProfile
import org.example.project.data.remote.toThemeMode
import org.example.project.data.remote.toUpdateRequest
import org.example.project.data.remote.toProduct
import org.example.project.data.remote.toSummary
import org.example.project.ui.signup.SignupData

private val CouponErrors = setOf("INVALID_COUPON", "COUPON_MIN_SUBTOTAL")
private const val SearchDebounceMs = 300L

// Estado do app vindo do backend e as ações que conversam com ele.
// As telas continuam recebendo modelos simples; aqui ficam as chamadas e o tratamento de erro.
class RangoStore(private val api: RangoApi, private val scope: CoroutineScope) {
    var userName by mutableStateOf("")
        private set
    var products by mutableStateOf(listOf<Product>())
        private set
    // Do favorito mais antigo para o mais novo (o conjunto mantém a ordem de inclusão).
    var favoriteIds by mutableStateOf(setOf<String>())
        private set
    var favoritesLoading by mutableStateOf(false)
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

    // Busca: termo e filtros ficam aqui para sobreviver à ida ao produto e à volta.
    var searchQuery by mutableStateOf("")
        private set
    var searchFilters by mutableStateOf(SearchFilters())
        private set
    // null = nada buscado ainda (termo vazio e sem filtros): a tela mostra recentes e sugestões.
    var searchResults by mutableStateOf<List<Product>?>(null)
        private set
    var searching by mutableStateOf(false)
        private set
    var recentSearches by mutableStateOf(listOf<String>())
        private set
    var suggestions by mutableStateOf(listOf<SearchSuggestion>())
        private set
    private var searchJob: Job? = null

    // Perfil: dados da conta, tema salvo e notificações não lidas (sino e dica da linha).
    var profile by mutableStateOf<UserProfile?>(null)
        private set
    var theme by mutableStateOf(ThemeMode.System)
        private set
    var unreadNotifications by mutableStateOf(0)
        private set
    // Foto de perfil já decodificada; nula mostra as iniciais.
    var avatar by mutableStateOf<ImageBitmap?>(null)
        private set
    private var avatarUrl: String? = null
    // Configurações: quais notificações o usuário quer receber.
    var notificationPrefs by mutableStateOf(NotificationPrefs())
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

    // Sair da conta: revoga a sessão e limpa os dados do usuário anterior.
    suspend fun logout() {
        api.logout()
        clear()
        message = "Você saiu da conta."
    }

    private fun clear() {
        userName = ""
        profile = null
        avatar = null
        avatarUrl = null
        theme = ThemeMode.System
        unreadNotifications = 0
        notificationPrefs = NotificationPrefs()
        products = emptyList()
        favoriteIds = emptySet()
        favoritesLoading = false
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
        searchJob?.cancel()
        searchQuery = ""
        searchFilters = SearchFilters()
        searchResults = null
        searching = false
        recentSearches = emptyList()
        suggestions = emptyList()
    }

    // Carrega tudo o que a Home e o checkout precisam, em paralelo; devolve se deu certo.
    private suspend fun loadAll(): Boolean {
        // coroutineScope: se uma chamada falhar, as outras são canceladas e o erro chega ao safely.
        return safely {
            coroutineScope {
                val home = async { api.home() }
                val cartDto = async { api.cart() }
                val addressList = async { api.addresses() }
                val paymentList = async { api.paymentMethods() }
                // Pedidos em andamento: badge da aba "Pedidos".
                val live = async { api.orders("live") }
                // Favoritos: badge da aba "Favoritos", mesmo dos produtos que não estão na Home.
                val favorites = async { api.favorites() }
                // Configurações já na entrada, para o app abrir no tema salvo.
                val settings = async { api.settings() }

                val homeDto = home.await()
                userName = homeDto.user.name
                categorySlugs = homeDto.categories.associate { it.name to it.slug }
                mergeProducts(homeDto.sections.flatMap { it.products })
                setFavorites(favorites.await())

                addresses = addressList.await().map { it.toAddress() }
                selectedAddressId = addressList.await().firstOrNull { it.isSelected }?.id ?: homeDto.selectedAddress?.id
                payments = paymentList.await().mapNotNull { it.toOption() }
                payment = payment?.let { current -> payments.firstOrNull { it.id == current.id } } ?: payments.firstOrNull()
                setCart(cartDto.await().toCart())
                liveOrders = live.await().map { it.toSummary() }
                applySettings(settings.await())
            }
        } != null
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
        message = if (favorite) "Adicionado aos favoritos." else "Removido dos favoritos."
    }

    // Lista da tela "Favoritos": os mais recentes primeiro.
    val favorites: List<Product>
        get() {
            val byId = products.associateBy { it.id }
            return favoriteIds.reversed().mapNotNull { byId[it] }
        }

    fun loadFavorites() {
        favoritesLoading = true
        scope.launch {
            safely { setFavorites(api.favorites()) }
            favoritesLoading = false
        }
    }

    private fun setFavorites(list: List<ProductDto>) {
        mergeProducts(list)
        // A API manda do mais recente para o mais antigo; o conjunto guarda ao contrário.
        favoriteIds = list.asReversed().map { it.id }.toSet()
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

    // Busca

    // Abertura da tela: buscas recentes e sugestões ("mais pedidos").
    fun loadSearchHome() {
        launchSafely {
            coroutineScope {
                val recent = async { api.recentSearches() }
                val names = async { api.searchSuggestions() }
                recentSearches = recent.await()
                suggestions = names.await().take(4).map { name ->
                    val product = products.firstOrNull { it.name == name }
                    val hint = product?.let { "${it.restaurant} · ${formatRating(it.rating)} · ${formatMoney(it.price)}" }
                    SearchSuggestion(name, hint.orEmpty())
                }
            }
        }
    }

    // Digitação: espera o usuário parar de digitar antes de buscar.
    fun changeQuery(text: String) {
        searchQuery = text
        runSearch(debounce = true)
    }

    fun applyFilters(filters: SearchFilters) {
        searchFilters = filters
        runSearch(debounce = false)
    }

    // "Ver tudo" da Home: abre a busca sem termo e com os filtros da seção.
    fun startSearch(filters: SearchFilters) {
        searchQuery = ""
        applyFilters(filters)
    }

    // Termo confirmado (teclado ou produto aberto dos resultados) entra nas buscas recentes.
    fun saveRecentSearch() {
        val term = searchQuery.trim()
        if (term.isEmpty()) return
        launchSafely { recentSearches = api.addRecentSearch(term) }
    }

    fun clearRecentSearches() {
        val previous = recentSearches
        recentSearches = emptyList()
        scope.launch {
            try {
                api.clearRecentSearches()
            } catch (e: ApiException) {
                recentSearches = previous
                message = e.message
            }
        }
    }

    private fun runSearch(debounce: Boolean) {
        searchJob?.cancel()
        if (searchQuery.isBlank() && searchFilters.isDefault) {
            searchResults = null
            searching = false
            return
        }
        searching = true
        val query = searchQuery
        val filters = searchFilters
        searchJob = scope.launch {
            if (debounce) delay(SearchDebounceMs)
            safely {
                val list = api.searchProducts(
                    query = query,
                    category = filters.category.takeIf { it != ProductCategories.first() }?.let { categorySlugs[it] ?: it },
                    maxPrice = filters.maxPrice.takeIf { it != SearchMaxPrice },
                    minRating = filters.minRating.takeIf { it > 0.0 },
                    maxDeliveryTime = filters.maxTime.takeIf { it != AnyDeliveryTime },
                    sort = filters.sort.apiValue,
                )
                // Também entra no catálogo local, para o detalhe do produto abrir na hora.
                mergeProducts(list)
                searchResults = list.map { it.toProduct() }
            }
            searching = false
        }
    }

    // Perfil

    // Abertura da aba: dados da conta, tema e não lidas, em paralelo.
    fun loadProfile() {
        launchSafely {
            coroutineScope {
                val me = async { api.me() }
                val settings = async { api.settings() }
                val unread = async { api.unreadNotifications() }
                setProfile(me.await().toProfile())
                applySettings(settings.await())
                unreadNotifications = unread.await()
            }
        }
    }

    // Editar perfil

    private suspend fun setProfile(value: UserProfile) {
        profile = value
        userName = value.name
        val url = value.avatarUrl
        if (url == avatarUrl) return
        avatarUrl = url
        // Foto nova: baixa e decodifica; se falhar, as iniciais continuam aparecendo.
        avatar = url?.let { path -> runCatching { api.download(path).decodeToImageBitmap() }.getOrNull() }
    }

    // Devolve os erros por campo (vazio = salvo), como no login e no cadastro.
    suspend fun updateProfile(name: String, email: String, phone: String): Map<String, String> = try {
        setProfile(api.updateProfile(UpdateProfileRequest(name.trim(), email.trim(), phone.trim())).toProfile())
        message = "Perfil atualizado com sucesso."
        emptyMap()
    } catch (e: ApiException) {
        when {
            e.code == "EMAIL_IN_USE" -> mapOf("email" to e.message)
            e.fields.isNotEmpty() -> e.fields
            else -> {
                message = e.message
                mapOf("" to e.message)
            }
        }
    }

    // Foto tirada pela câmera (JPEG quadrado vindo da plataforma).
    fun changeAvatar(jpeg: ByteArray) {
        launchSafely {
            val updated = api.uploadAvatar(jpeg).toProfile()
            // Mostra a foto local na hora, sem baixar de novo do servidor.
            avatar = jpeg.decodeToImageBitmap()
            avatarUrl = updated.avatarUrl
            setProfile(updated)
            message = "Foto atualizada."
        }
    }

    fun removeAvatar() {
        launchSafely {
            setProfile(api.removeAvatar().toProfile())
            message = "Foto removida."
        }
    }

    // Configurações

    private fun applySettings(dto: SettingsDto) {
        theme = dto.toThemeMode()
        notificationPrefs = dto.notifications.toPrefs()
    }

    // Troca na hora e volta ao valor anterior se o servidor recusar.
    fun changeTheme(mode: ThemeMode) {
        val previous = theme
        if (mode == previous) return
        theme = mode
        scope.launch {
            try {
                api.updateSettings(UpdateSettingsRequest(theme = mode.apiValue))
            } catch (e: ApiException) {
                theme = previous
                message = e.message
            }
        }
    }

    fun changeNotificationPrefs(prefs: NotificationPrefs) {
        val previous = notificationPrefs
        notificationPrefs = prefs
        scope.launch {
            try {
                api.updateSettings(UpdateSettingsRequest(notifications = prefs.toDto()))
            } catch (e: ApiException) {
                notificationPrefs = previous
                message = e.message
            }
        }
    }

    // "Sincronizar agora": busca de novo no servidor os dados que o app guarda em memória.
    fun sync() {
        scope.launch {
            if (loadAll()) {
                loadProfile()
                message = "Dados sincronizados."
            }
        }
    }

    // "Limpar dados locais": apaga a sessão salva no aparelho e os dados em memória.
    suspend fun clearLocalData() {
        api.logout()
        clear()
        message = "Dados locais apagados."
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

    suspend fun updateAddress(id: Int, form: AddressForm): Boolean = safely {
        val saved = api.updateAddress(id, form.toUpdateRequest()).toAddress()
        addresses = addresses.map { if (it.id == id) saved else it }
        message = "Endereço atualizado."
    } != null

    // O servidor escolhe outro endereço de entrega se o removido era o selecionado: recarrega a lista.
    suspend fun deleteAddress(id: Int): Boolean = safely {
        api.deleteAddress(id)
        val list = api.addresses()
        addresses = list.map { it.toAddress() }
        selectedAddressId = list.firstOrNull { it.isSelected }?.id
        message = "Endereço removido."
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
        val result = api.reorder(id)
        setCart(result.cart.toCart())
        couponInput = ""
        message = if (result.skippedProductIds.isEmpty()) {
            "Itens adicionados ao carrinho."
        } else {
            "Itens adicionados. Alguns produtos não estão mais disponíveis."
        }
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
