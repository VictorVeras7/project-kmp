package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.data.CartItem
import org.example.project.data.Order
import org.example.project.data.PaymentMethod
import org.example.project.data.SampleAddresses
import org.example.project.data.SampleProducts
import org.example.project.data.add
import org.example.project.data.changeQuantity
import org.example.project.data.isValidCoupon
import org.example.project.data.itemCount
import org.example.project.data.orderId
import org.example.project.data.remove
import org.example.project.data.totals
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
import org.example.project.ui.theme.RangoTheme
import org.example.project.ui.welcome.WelcomeScreen

private enum class Screen { Welcome, Login, Signup, Home, ProductDetail, Cart, Checkout, Address, Payment, OrderPlaced, Tracking }

@Composable
@Preview
fun App() {
    var screen by remember { mutableStateOf(Screen.Welcome) }
    var productId by remember { mutableStateOf<String?>(null) }
    // Tela de onde o produto foi aberto, para o voltar retornar ao lugar certo.
    var productOrigin by remember { mutableStateOf(Screen.Home) }
    // Carrinho compartilhado entre as telas.
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    var coupon by remember { mutableStateOf("") }
    var couponApplied by remember { mutableStateOf(false) }
    // Endereço e pagamento escolhidos no checkout.
    var addresses by remember { mutableStateOf(SampleAddresses) }
    var addressIndex by remember { mutableStateOf(0) }
    var payment by remember { mutableStateOf(PaymentMethod.Pix) }
    // Pedidos feitos na sessão; o primeiro da lista é o mais recente.
    var orders by remember { mutableStateOf(listOf<Order>()) }
    var favoriteIds by remember { mutableStateOf(setOf<String>()) }
    // Mantém o estado da Home (categoria e scroll) ao abrir e voltar do produto.
    val stateHolder = rememberSaveableStateHolder()

    fun toggleFavorite(id: String) {
        favoriteIds = if (id in favoriteIds) favoriteIds - id else favoriteIds + id
    }

    fun openProduct(id: String, from: Screen) {
        productId = id
        productOrigin = from
        screen = Screen.ProductDetail
    }

    fun productById(id: String?) = SampleProducts.firstOrNull { it.id == id } ?: SampleProducts.first()

    RangoTheme {
        stateHolder.SaveableStateProvider(screen.name) {
            when (screen) {
                Screen.Welcome -> WelcomeScreen(
                    onLogin = { screen = Screen.Login },
                    onSignup = { screen = Screen.Signup },
                )
                Screen.Login -> LoginScreen(
                    onBack = { screen = Screen.Welcome },
                    onLoginSuccess = { screen = Screen.Home },
                    onForgotPassword = {},
                    onSignup = { screen = Screen.Signup },
                )
                Screen.Signup -> SignupScreen(
                    onBack = { screen = Screen.Welcome },
                    onSignupSuccess = { _, _, _ -> screen = Screen.Home },
                    onLogin = { screen = Screen.Login },
                )
                Screen.Home -> HomeScreen(
                    userName = "Marina Alves",
                    address = addresses[addressIndex].short,
                    cartCount = cartItems.itemCount(),
                    favoriteIds = favoriteIds,
                    onToggleFavorite = ::toggleFavorite,
                    onAddToCart = { id -> cartItems = cartItems.add(productById(id), 1) },
                    onOpenProduct = { id -> openProduct(id, Screen.Home) },
                    onOpenCart = { screen = Screen.Cart },
                    onOpenNotifications = {},
                    onOpenSearch = {},
                )
                Screen.ProductDetail -> {
                    val product = productById(productId)
                    ProductDetailScreen(
                        product = product,
                        isFavorite = product.id in favoriteIds,
                        onBack = { screen = productOrigin },
                        onToggleFavorite = { toggleFavorite(product.id) },
                        onAddToCart = { quantity, addonIds, notes ->
                            cartItems = cartItems.add(product, quantity, addonIds, notes)
                            screen = productOrigin
                        },
                    )
                }
                Screen.Cart -> CartScreen(
                    items = cartItems,
                    coupon = coupon,
                    couponApplied = couponApplied,
                    onBack = { screen = Screen.Home },
                    onClear = {
                        cartItems = emptyList()
                        coupon = ""
                        couponApplied = false
                    },
                    onChangeQuantity = { key, delta -> cartItems = cartItems.changeQuantity(key, delta) },
                    onRemove = { key -> cartItems = cartItems.remove(key) },
                    onOpenProduct = { id -> openProduct(id, Screen.Cart) },
                    onCouponChange = {
                        coupon = it
                        couponApplied = false
                    },
                    onApplyCoupon = { couponApplied = isValidCoupon(coupon) },
                    onCheckout = { screen = Screen.Checkout },
                    onExplore = { screen = Screen.Home },
                )
                Screen.Checkout -> CheckoutScreen(
                    items = cartItems,
                    couponApplied = couponApplied,
                    address = addresses[addressIndex],
                    payment = payment,
                    onBack = { screen = Screen.Cart },
                    onChangeAddress = { screen = Screen.Address },
                    onChangePayment = { screen = Screen.Payment },
                    onOrderConfirmed = {
                        val order = Order(
                            id = orderId(orders.size),
                            items = cartItems,
                            total = cartItems.totals(couponApplied).total,
                            address = addresses[addressIndex],
                            payment = payment,
                        )
                        orders = listOf(order) + orders
                        cartItems = emptyList()
                        coupon = ""
                        couponApplied = false
                        screen = Screen.OrderPlaced
                    },
                )
                Screen.Address -> AddressScreen(
                    addresses = addresses,
                    selectedIndex = addressIndex,
                    onSelect = { addressIndex = it },
                    onSaveAddress = { address ->
                        addresses = addresses + address
                        addressIndex = addresses.lastIndex
                    },
                    onBack = { screen = Screen.Checkout },
                )
                Screen.Payment -> PaymentScreen(
                    selected = payment,
                    onSelect = { payment = it },
                    onBack = { screen = Screen.Checkout },
                )
                Screen.OrderPlaced -> OrderPlacedScreen(
                    order = orders.first(),
                    onTrackOrder = { screen = Screen.Tracking },
                    onBackHome = { screen = Screen.Home },
                )
                Screen.Tracking -> {
                    val order = orders.first()
                    OrderTrackingScreen(
                        order = order,
                        onAdvance = {
                            orders = orders.map { if (it.id == order.id) it.copy(step = it.step.next) else it }
                        },
                        onBack = { screen = Screen.OrderPlaced },
                    )
                }
            }
        }
    }
}
