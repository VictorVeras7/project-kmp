package org.example.project

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.data.OrderPollIntervalMs
import org.example.project.data.ThemeMode
import org.example.project.data.UserProfile
import org.example.project.data.itemCount
import org.example.project.data.remote.RangoApi
import org.example.project.ui.cart.CartScreen
import org.example.project.ui.checkout.AddressScreen
import org.example.project.ui.checkout.CheckoutScreen
import org.example.project.ui.checkout.PaymentScreen
import org.example.project.ui.components.BottomNavBar
import org.example.project.ui.components.MainTab
import org.example.project.ui.favorites.FavoritesScreen
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.login.LoginScreen
import org.example.project.ui.order.OrderDetailScreen
import org.example.project.ui.order.OrderPlacedScreen
import org.example.project.ui.order.OrderTrackingScreen
import org.example.project.ui.order.OrdersScreen
import org.example.project.ui.product.ProductDetailScreen
import org.example.project.platform.rememberCameraLauncher
import org.example.project.ui.profile.EditProfileScreen
import org.example.project.ui.profile.ProfileScreen
import org.example.project.ui.search.SearchScreen
import org.example.project.ui.settings.NotificationSettingsScreen
import org.example.project.ui.settings.SettingsScreen
import org.example.project.ui.settings.ThemeScreen
import org.example.project.ui.signup.SignupScreen
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme
import org.example.project.ui.welcome.WelcomeScreen

// Intervalo entre as tentativas de reconectar enquanto o app está offline.
private const val OfflineRetryMs = 10_000L
private val OfflineBannerColor = Color(0xFF3A3A3F)

private enum class Screen {
    Splash, Welcome, Login, Signup, Home, ProductDetail, Cart, Checkout, Address, Payment, OrderPlaced, Tracking,
    Orders, OrderDetail, Search, Favorites, Profile, Settings, ThemeSettings, NotificationSettings, EditProfile,
}

// Telas com a barra de navegação inferior e a aba que cada uma marca.
private val TabScreens = mapOf(
    Screen.Home to MainTab.Home,
    Screen.Search to MainTab.Search,
    Screen.Orders to MainTab.Orders,
    Screen.Favorites to MainTab.Favorites,
    Screen.Profile to MainTab.Profile,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun App(onDarkThemeChange: (Boolean) -> Unit = {}) {
    val scope = rememberCoroutineScope()
    val store = remember { RangoStore(RangoApi(), scope) }
    var screen by remember { mutableStateOf(Screen.Splash) }
    var productId by remember { mutableStateOf<String?>(null) }
    // Tela de onde o produto foi aberto, para o voltar retornar ao lugar certo.
    var productOrigin by remember { mutableStateOf(Screen.Home) }
    // Mesma ideia para o carrinho (Home ou "Pedir novamente") e o acompanhamento.
    var cartOrigin by remember { mutableStateOf(Screen.Home) }
    var trackingOrigin by remember { mutableStateOf(Screen.OrderPlaced) }
    // Endereços e pagamento abrem do checkout, do perfil ou das configurações.
    var accountOrigin by remember { mutableStateOf(Screen.Checkout) }
    // Tema abre do perfil ou das configurações.
    var themeOrigin by remember { mutableStateOf(Screen.Settings) }
    // Editar perfil abre do perfil ou de "Dados pessoais" nas configurações.
    var editProfileOrigin by remember { mutableStateOf(Screen.Profile) }
    var detailId by remember { mutableStateOf<Int?>(null) }
    // Mantém o estado da Home (categoria e scroll) ao abrir e voltar do produto.
    val stateHolder = rememberSaveableStateHolder()
    val snackbar = remember { SnackbarHostState() }

    // Abertura: com sessão salva vai direto para a Home; senão, boas-vindas.
    LaunchedEffect(Unit) {
        store.onSessionExpired { screen = Screen.Login }
        screen = if (store.restoreSession()) Screen.Home else Screen.Welcome
    }

    // Erros da API aparecem num aviso na parte de baixo da tela.
    LaunchedEffect(store.message) {
        store.message?.let {
            snackbar.showSnackbar(it)
            store.message = null
        }
    }

    // Enquanto o pedido está em andamento, consulta o servidor para atualizar a etapa.
    // Só com o app na tela: em segundo plano o Android corta a rede e cada consulta viraria um aviso de erro.
    val order = store.order
    val lifecycle by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val visible = lifecycle.isAtLeast(Lifecycle.State.STARTED)
    LaunchedEffect(screen, order?.id, order?.isLive, visible) {
        if (visible && order != null && order.isLive && (screen == Screen.OrderPlaced || screen == Screen.Tracking)) {
            while (true) {
                delay(OrderPollIntervalMs)
                store.refreshOrder()
            }
        }
    }

    // Offline: tenta voltar a falar com o servidor de tempos em tempos (só com o app na tela).
    LaunchedEffect(store.offline, visible) {
        while (store.offline && visible) {
            delay(OfflineRetryMs)
            store.retryConnection()
        }
    }

    // Ao abrir as abas, atualiza os pedidos (lista e badge de "Pedidos").
    // Offline não busca: a faixa já avisa, e cada busca viraria um aviso de erro.
    LaunchedEffect(screen) {
        if (store.offline) return@LaunchedEffect
        if (screen in TabScreens) store.loadOrders()
        if (screen == Screen.Search) store.loadSearchHome()
        if (screen == Screen.Favorites) store.loadFavorites()
        if (screen == Screen.Profile) store.loadProfile()
        if (screen == Screen.EditProfile && store.profile == null) store.loadProfile()
        if (screen == Screen.Cart) store.loadCart()
    }

    fun openCart(from: Screen) {
        cartOrigin = from
        screen = Screen.Cart
    }

    fun openAccountScreen(target: Screen, from: Screen) {
        accountOrigin = from
        screen = target
    }

    fun comingSoon(feature: String) {
        store.message = "$feature estará disponível em breve."
    }

    fun openEditProfile(from: Screen) {
        editProfileOrigin = from
        screen = Screen.EditProfile
    }

    // Foto de perfil pela câmera (ponto de plataforma: expect/actual).
    val takePhoto = rememberCameraLauncher(
        onPhoto = store::changeAvatar,
        onDenied = { store.message = "Permita o acesso à câmera para tirar a foto." },
    )

    fun openTheme(from: Screen) {
        themeOrigin = from
        screen = Screen.ThemeSettings
    }

    // Destino do voltar (botão da tela e botão do sistema). Nulo na Home e nas boas-vindas: o sistema fecha o app.
    val backTarget: Screen? = when (screen) {
        Screen.Splash, Screen.Welcome, Screen.Home -> null
        Screen.Login, Screen.Signup -> Screen.Welcome
        Screen.Search, Screen.Orders, Screen.Favorites, Screen.Profile, Screen.OrderPlaced -> Screen.Home
        Screen.ProductDetail -> productOrigin
        Screen.Cart -> cartOrigin
        Screen.Checkout -> Screen.Cart
        Screen.Address, Screen.Payment -> accountOrigin
        Screen.Tracking -> trackingOrigin
        Screen.OrderDetail -> Screen.Orders
        Screen.Settings -> Screen.Profile
        Screen.ThemeSettings -> themeOrigin
        Screen.NotificationSettings -> Screen.Settings
        Screen.EditProfile -> editProfileOrigin
    }

    fun goBack() {
        backTarget?.let { screen = it }
    }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = backTarget != null,
        onBackCompleted = ::goBack,
    )

    fun reorder(id: Int, from: Screen) {
        scope.launch { if (store.reorder(id)) openCart(from) }
    }

    fun openProduct(id: String, from: Screen) {
        productId = id
        productOrigin = from
        store.loadProduct(id)
        screen = Screen.ProductDetail
    }

    val tab = TabScreens[screen]
    // Com o teclado aberto (digitando na busca), a barra inferior sai para dar espaço aos resultados.
    val showNavBar = tab != null && !WindowInsets.isImeVisible

    // Tema salvo nas configurações; "Sistema" segue o aparelho.
    val darkTheme = when (store.theme) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }
    // A plataforma ajusta a barra de status (ícones claros no escuro).
    LaunchedEffect(darkTheme) { onDarkThemeChange(darkTheme) }

    RangoTheme(darkTheme = darkTheme) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                if (store.offline) OfflineBanner()
                Box(
                    Modifier
                        .weight(1f)
                        // A faixa já ocupa o espaço da barra de status; as telas abaixo não somam de novo.
                        .then(if (store.offline) Modifier.consumeWindowInsets(WindowInsets.statusBars) else Modifier),
                ) {
                    stateHolder.SaveableStateProvider(screen.name) {
                        when (screen) {
                            Screen.Splash -> Splash()
                            Screen.Welcome -> WelcomeScreen(
                                onLogin = { screen = Screen.Login },
                                onSignup = { screen = Screen.Signup },
                            )
                            Screen.Login -> LoginScreen(
                                onBack = ::goBack,
                                authenticate = store::login,
                                onLoginSuccess = { screen = Screen.Home },
                                onForgotPassword = {},
                                onSignup = { screen = Screen.Signup },
                            )
                            Screen.Signup -> SignupScreen(
                                onBack = ::goBack,
                                register = store::register,
                                onSignupSuccess = { screen = Screen.Home },
                                onLogin = { screen = Screen.Login },
                            )
                            Screen.Home -> HomeScreen(
                                userName = store.userName,
                                address = store.selectedAddress?.short.orEmpty(),
                                cartCount = store.cart.items.itemCount(),
                                favoriteIds = store.favoriteIds,
                                onToggleFavorite = store::toggleFavorite,
                                onAddToCart = { id -> store.addToCart(id, 1) },
                                onOpenProduct = { id -> openProduct(id, Screen.Home) },
                                onOpenCart = { openCart(Screen.Home) },
                                onOpenNotifications = {},
                                onOpenSearch = { screen = Screen.Search },
                                products = store.products,
                                onCategoryChange = store::loadCategory,
                                onSeeAll = { filters ->
                                    store.startSearch(filters)
                                    screen = Screen.Search
                                },
                            )
                            Screen.Search -> SearchScreen(
                                query = store.searchQuery,
                                filters = store.searchFilters,
                                results = store.searchResults,
                                searching = store.searching,
                                recentSearches = store.recentSearches,
                                suggestions = store.suggestions,
                                favoriteIds = store.favoriteIds,
                                onQueryChange = store::changeQuery,
                                onSubmit = store::saveRecentSearch,
                                onApplyFilters = store::applyFilters,
                                onFiltersApplied = { store.message = "Filtros aplicados." },
                                onClearRecent = store::clearRecentSearches,
                                onOpenProduct = { id ->
                                    // Abrir um resultado também guarda o termo nas buscas recentes.
                                    store.saveRecentSearch()
                                    openProduct(id, Screen.Search)
                                },
                                onToggleFavorite = store::toggleFavorite,
                                onAddToCart = { id -> store.addToCart(id, 1) },
                            )
                            Screen.ProductDetail -> {
                                val product = store.products.firstOrNull { it.id == productId }
                                if (product == null) {
                                    Splash()
                                } else {
                                    ProductDetailScreen(
                                        product = product,
                                        isFavorite = product.id in store.favoriteIds,
                                        onBack = ::goBack,
                                        onToggleFavorite = { store.toggleFavorite(product.id) },
                                        onAddToCart = { quantity, addonIds, notes ->
                                            store.addToCart(product.id, quantity, addonIds, notes)
                                            screen = productOrigin
                                        },
                                        addons = store.addons,
                                    )
                                }
                            }
                            Screen.Cart -> CartScreen(
                                items = store.cart.items,
                                coupon = store.couponInput,
                                couponApplied = store.cart.couponApplied,
                                onBack = ::goBack,
                                onClear = store::clearCart,
                                onChangeQuantity = store::changeQuantity,
                                onRemove = store::removeLine,
                                onOpenProduct = { id -> openProduct(id, Screen.Cart) },
                                onCouponChange = store::changeCoupon,
                                onApplyCoupon = store::applyCoupon,
                                onCheckout = {
                                    // Sem rede o carrinho continua visível, mas o pedido precisa do servidor.
                                    if (store.offline) {
                                        store.message = "Você está offline. Conecte-se para finalizar o pedido."
                                    } else {
                                        screen = Screen.Checkout
                                    }
                                },
                                onExplore = { screen = Screen.Home },
                                totals = store.cart.totals,
                                products = store.products,
                                addons = store.addons,
                            )
                            Screen.Checkout -> CheckoutScreen(
                                items = store.cart.items,
                                couponApplied = store.cart.couponApplied,
                                address = store.selectedAddress,
                                payment = store.payment,
                                onBack = ::goBack,
                                onChangeAddress = { openAccountScreen(Screen.Address, Screen.Checkout) },
                                onChangePayment = { openAccountScreen(Screen.Payment, Screen.Checkout) },
                                onConfirmOrder = {
                                    val ok = store.confirmOrder()
                                    if (ok) screen = Screen.OrderPlaced
                                    ok
                                },
                                totals = store.cart.totals,
                                products = store.products,
                                addons = store.addons,
                            )
                            Screen.Address -> AddressScreen(
                                addresses = store.addresses,
                                selectedIndex = store.addresses.indexOf(store.selectedAddress),
                                onSelect = store::selectAddress,
                                onSaveAddress = store::saveAddress,
                                onUpdateAddress = store::updateAddress,
                                onDeleteAddress = store::deleteAddress,
                                onBack = ::goBack,
                            )
                            Screen.Payment -> PaymentScreen(
                                options = store.payments,
                                selected = store.payment,
                                onSelect = { store.payment = it },
                                onBack = ::goBack,
                            )
                            Screen.OrderPlaced -> store.order?.let { placed ->
                                OrderPlacedScreen(
                                    order = placed,
                                    onTrackOrder = {
                                        trackingOrigin = Screen.OrderPlaced
                                        screen = Screen.Tracking
                                    },
                                    onBackHome = { screen = Screen.Home },
                                )
                            }
                            Screen.Tracking -> store.order?.let { tracked ->
                                OrderTrackingScreen(order = tracked, onBack = ::goBack)
                            }
                            Screen.Orders -> OrdersScreen(
                                liveOrders = store.liveOrders,
                                pastOrders = store.pastOrders,
                                loading = store.ordersLoading,
                                onOpenOrder = { id ->
                                    detailId = id
                                    store.loadOrderDetail(id)
                                    screen = Screen.OrderDetail
                                },
                                onTrackOrder = { id ->
                                    scope.launch {
                                        if (store.trackOrder(id)) {
                                            trackingOrigin = Screen.Orders
                                            screen = Screen.Tracking
                                        }
                                    }
                                },
                                onReorder = { id -> reorder(id, Screen.Orders) },
                                onExplore = { screen = Screen.Home },
                            )
                            Screen.Favorites -> FavoritesScreen(
                                favorites = store.favorites,
                                loading = store.favoritesLoading,
                                onOpenProduct = { id -> openProduct(id, Screen.Favorites) },
                                onToggleFavorite = store::toggleFavorite,
                                onAddToCart = { id -> store.addToCart(id, 1) },
                                onExplore = { screen = Screen.Home },
                            )
                            Screen.Profile -> ProfileScreen(
                                // Até GET /me responder, mostra o nome que veio da Home.
                                profile = store.profile ?: UserProfile(store.userName, "", ""),
                                avatar = store.avatar,
                                addressCount = store.addresses.size,
                                paymentCount = store.payments.size,
                                unreadNotifications = store.unreadNotifications,
                                theme = store.theme,
                                onOpenNotifications = { comingSoon("Notificações") },
                                onEditProfile = { openEditProfile(Screen.Profile) },
                                onAddresses = { openAccountScreen(Screen.Address, Screen.Profile) },
                                onPayments = { openAccountScreen(Screen.Payment, Screen.Profile) },
                                onSettings = { screen = Screen.Settings },
                                onTheme = { openTheme(Screen.Profile) },
                                onHelp = { store.message = "Central de ajuda em breve." },
                                onLogout = {
                                    scope.launch {
                                        store.logout()
                                        screen = Screen.Welcome
                                    }
                                },
                            )
                            Screen.Settings -> SettingsScreen(
                                theme = store.theme,
                                onBack = ::goBack,
                                onTheme = { openTheme(Screen.Settings) },
                                onNotifications = { screen = Screen.NotificationSettings },
                                onLanguage = { store.message = "Por enquanto, o app está disponível apenas em português." },
                                onPersonalData = { openEditProfile(Screen.Settings) },
                                onAddresses = { openAccountScreen(Screen.Address, Screen.Settings) },
                                onPayments = { openAccountScreen(Screen.Payment, Screen.Settings) },
                                onSync = store::sync,
                                onClearLocalData = {
                                    scope.launch {
                                        store.clearLocalData()
                                        screen = Screen.Welcome
                                    }
                                },
                                onHelp = { store.message = "Central de ajuda em breve." },
                                onTerms = { comingSoon("Termos e privacidade") },
                            )
                            Screen.ThemeSettings -> ThemeScreen(
                                theme = store.theme,
                                onSelect = store::changeTheme,
                                onBack = ::goBack,
                            )
                            Screen.NotificationSettings -> NotificationSettingsScreen(
                                prefs = store.notificationPrefs,
                                onChange = store::changeNotificationPrefs,
                                onBack = ::goBack,
                            )
                            Screen.EditProfile -> {
                                val profile = store.profile
                                if (profile == null) {
                                    Splash()
                                } else {
                                    EditProfileScreen(
                                        profile = profile,
                                        avatar = store.avatar,
                                        onBack = ::goBack,
                                        onSave = store::updateProfile,
                                        onTakePhoto = takePhoto,
                                        onRemovePhoto = store::removeAvatar,
                                    )
                                }
                            }
                            Screen.OrderDetail -> {
                                val detail = store.orderDetail?.takeIf { it.id == detailId }
                                if (detail == null) {
                                    Splash()
                                } else {
                                    OrderDetailScreen(
                                        order = detail,
                                        onBack = ::goBack,
                                        onReorder = { if (store.reorder(detail.id)) openCart(Screen.OrderDetail) },
                                    )
                                }
                            }
                        }
                    }
                }
                if (showNavBar && tab != null) {
                    BottomNavBar(
                        selected = tab,
                        onSelect = { selected ->
                            when (selected) {
                                MainTab.Home -> screen = Screen.Home
                                MainTab.Search -> screen = Screen.Search
                                MainTab.Orders -> screen = Screen.Orders
                                MainTab.Favorites -> screen = Screen.Favorites
                                MainTab.Profile -> screen = Screen.Profile
                            }
                        },
                        badges = mapOf(MainTab.Orders to store.liveOrders.size, MainTab.Favorites to store.favoriteIds.size),
                    )
                }
            }
            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    // Nas abas, o aviso aparece acima da barra inferior.
                    .padding(bottom = if (showNavBar) 64.dp else 0.dp),
            )
        }
    }
}

// Faixa do protótipo, logo abaixo da barra de status, enquanto o app mostra as cópias locais.
@Composable
private fun OfflineBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .background(OfflineBannerColor)
            .padding(horizontal = 18.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(RangoIcons.CloudOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
        Text("Você está offline", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
        Text(
            "Exibindo dados salvos no dispositivo.",
            color = Color(0xFFC7C7CC),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// Tela de espera enquanto a sessão salva é verificada ou um produto carrega.
@Composable
private fun Splash() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = RangoColors.Primary)
        }
    }
}
