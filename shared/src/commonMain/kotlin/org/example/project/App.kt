package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.data.CartItem
import org.example.project.data.SampleProducts
import org.example.project.data.add
import org.example.project.data.changeQuantity
import org.example.project.data.isValidCoupon
import org.example.project.data.itemCount
import org.example.project.data.remove
import org.example.project.ui.cart.CartScreen
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.login.LoginScreen
import org.example.project.ui.product.ProductDetailScreen
import org.example.project.ui.signup.SignupScreen
import org.example.project.ui.theme.RangoTheme
import org.example.project.ui.welcome.WelcomeScreen

private enum class Screen { Welcome, Login, Signup, Home, ProductDetail, Cart }

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
                    address = "Rua das Palmeiras, 128",
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
                    onCheckout = {},
                    onExplore = { screen = Screen.Home },
                )
            }
        }
    }
}
