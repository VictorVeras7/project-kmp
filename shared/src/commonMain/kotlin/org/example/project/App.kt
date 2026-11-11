package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import org.example.project.data.OrderPollIntervalMs
import org.example.project.data.itemCount
import org.example.project.data.remote.RangoApi
import org.example.project.ui.cart.CartScreen
import org.example.project.ui.checkout.AddressScreen
import org.example.project.ui.checkout.CheckoutScreen
import org.example.project.ui.checkout.PaymentScreen
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.login.LoginScreen
import org.example.project.ui.order.OrderPlacedScreen
import org.example.project.ui.order.OrderTrackingScreen
import org.example.project.ui.product.ProductDetailScreen
import org.example.project.ui.signup.SignupScreen
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoTheme
import org.example.project.ui.welcome.WelcomeScreen

private enum class Screen {
    Splash, Welcome, Login, Signup, Home, ProductDetail, Cart, Checkout, Address, Payment, OrderPlaced, Tracking
}

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val store = remember { RangoStore(RangoApi(), scope) }
    var screen by remember { mutableStateOf(Screen.Splash) }
    var productId by remember { mutableStateOf<String?>(null) }
    // Tela de onde o produto foi aberto, para o voltar retornar ao lugar certo.
    var productOrigin by remember { mutableStateOf(Screen.Home) }
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
    val order = store.order
    LaunchedEffect(screen, order?.id, order?.isLive) {
        if (order != null && order.isLive && (screen == Screen.OrderPlaced || screen == Screen.Tracking)) {
            while (true) {
                delay(OrderPollIntervalMs)
                store.refreshOrder()
            }
        }
    }

    fun openProduct(id: String, from: Screen) {
        productId = id
        productOrigin = from
        store.loadProduct(id)
        screen = Screen.ProductDetail
    }

    RangoTheme {
        Box(Modifier.fillMaxSize()) {
            stateHolder.SaveableStateProvider(screen.name) {
                when (screen) {
                    Screen.Splash -> Splash()
                    Screen.Welcome -> WelcomeScreen(
                        onLogin = { screen = Screen.Login },
                        onSignup = { screen = Screen.Signup },
                    )
                    Screen.Login -> LoginScreen(
                        onBack = { screen = Screen.Welcome },
                        authenticate = store::login,
                        onLoginSuccess = { screen = Screen.Home },
                        onForgotPassword = {},
                        onSignup = { screen = Screen.Signup },
                    )
                    Screen.Signup -> SignupScreen(
                        onBack = { screen = Screen.Welcome },
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
                        onOpenCart = { screen = Screen.Cart },
                        onOpenNotifications = {},
                        onOpenSearch = {},
                        products = store.products,
                        onCategoryChange = store::loadCategory,
                    )
                    Screen.ProductDetail -> {
                        val product = store.products.firstOrNull { it.id == productId }
                        if (product == null) {
                            Splash()
                        } else {
                            ProductDetailScreen(
                                product = product,
                                isFavorite = product.id in store.favoriteIds,
                                onBack = { screen = productOrigin },
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
                        onBack = { screen = Screen.Home },
                        onClear = store::clearCart,
                        onChangeQuantity = store::changeQuantity,
                        onRemove = store::removeLine,
                        onOpenProduct = { id -> openProduct(id, Screen.Cart) },
                        onCouponChange = store::changeCoupon,
                        onApplyCoupon = store::applyCoupon,
                        onCheckout = { screen = Screen.Checkout },
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
                        onBack = { screen = Screen.Cart },
                        onChangeAddress = { screen = Screen.Address },
                        onChangePayment = { screen = Screen.Payment },
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
                        onBack = { screen = Screen.Checkout },
                    )
                    Screen.Payment -> PaymentScreen(
                        options = store.payments,
                        selected = store.payment,
                        onSelect = { store.payment = it },
                        onBack = { screen = Screen.Checkout },
                    )
                    Screen.OrderPlaced -> store.order?.let { placed ->
                        OrderPlacedScreen(
                            order = placed,
                            onTrackOrder = { screen = Screen.Tracking },
                            onBackHome = { screen = Screen.Home },
                        )
                    }
                    Screen.Tracking -> store.order?.let { tracked ->
                        OrderTrackingScreen(order = tracked, onBack = { screen = Screen.OrderPlaced })
                    }
                }
            }
            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
            )
        }
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
