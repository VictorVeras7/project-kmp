package org.example.project.data.remote

import kotlinx.serialization.Serializable

// Formatos das respostas e corpos da API, só com os campos que o app usa.
// Campos extras da resposta são ignorados (ver Json em RangoApi).

@Serializable
data class ErrorBody(val error: ErrorDto)

@Serializable
data class ErrorDto(val code: String, val message: String, val details: List<FieldErrorDto> = emptyList())

@Serializable
data class FieldErrorDto(val field: String, val message: String)

@Serializable
data class ListResponse<T>(val data: List<T>)

// Sessão

@Serializable
data class LoginRequest(val email: String, val password: String, val keepSignedIn: Boolean)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val passwordConfirmation: String,
    val acceptTerms: Boolean,
    val keepSignedIn: Boolean,
)

@Serializable
data class RefreshRequest(val refreshToken: String)

@Serializable
data class SessionDto(val accessToken: String, val refreshToken: String, val user: UserDto)

@Serializable
data class UserDto(val name: String, val email: String)

// Catálogo

@Serializable
data class HomeDto(
    val user: HomeUserDto,
    val selectedAddress: AddressDto? = null,
    val cartItemCount: Int,
    val categories: List<CategoryDto>,
    val sections: List<HomeSectionDto>,
)

@Serializable
data class HomeUserDto(val name: String)

@Serializable
data class CategoryDto(val name: String, val slug: String)

@Serializable
data class HomeSectionDto(val key: String, val products: List<ProductDto>)

@Serializable
data class DeliveryTimeDto(val min: Int, val max: Int)

@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val restaurant: String,
    val category: CategoryDto,
    val description: String,
    val price: Double,
    val oldPrice: Double? = null,
    val rating: Double,
    val deliveryTime: DeliveryTimeDto,
    val imageHue: Float,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    // Só vêm no detalhe do produto.
    val ingredients: String? = null,
    val addons: List<AddonDto>? = null,
)

@Serializable
data class AddonDto(val id: String, val name: String, val price: Double)

// Carrinho

@Serializable
data class CartDto(val items: List<CartLineDto>, val coupon: CouponDto? = null, val summary: CartSummaryDto)

@Serializable
data class CartLineDto(
    val id: Int,
    val product: CartProductDto,
    val quantity: Int,
    val addons: List<AddonDto>,
    val notes: String = "",
    val unitPrice: Double,
)

@Serializable
data class CartProductDto(val id: String)

@Serializable
data class CouponDto(val code: String, val applied: Boolean = true)

@Serializable
data class CartSummaryDto(val itemCount: Int, val subtotal: Double, val deliveryFee: Double, val discount: Double)

@Serializable
data class AddCartItemRequest(val productId: String, val quantity: Int, val addonIds: List<String>, val notes: String)

@Serializable
data class QuantityRequest(val quantity: Int)

@Serializable
data class CouponRequest(val code: String)

// Endereços e pagamento

@Serializable
data class AddressDto(
    val id: Int,
    val label: String,
    val line: String,
    val cityLine: String,
    val isSelected: Boolean = false,
)

@Serializable
data class NewAddressRequest(
    val label: String,
    val cep: String,
    val street: String,
    val complement: String?,
    val neighborhood: String,
    val city: String,
    val select: Boolean,
)

@Serializable
data class PaymentMethodDto(val id: Int, val type: String, val label: String, val detail: String)

// Pedidos

@Serializable
data class CreateOrderRequest(val paymentMethodId: Int)

@Serializable
data class OrderDto(
    val id: Int,
    val code: String,
    val status: String,
    val step: Int,
    val total: Double,
    val itemCount: Int,
    val hint: String,
    val timeline: List<TimelineStepDto>,
    val estimatedDelivery: EstimatedDeliveryDto? = null,
    val payment: OrderPaymentDto,
    val courier: CourierDto? = null,
    // Campos usados na tela de detalhes do pedido.
    val statusLabel: String = "",
    val createdAt: String = "",
    val items: List<OrderItemDto> = emptyList(),
    val address: AddressDto? = null,
    val amounts: OrderAmountsDto? = null,
)

@Serializable
data class TimelineStepDto(val step: Int, val reachedAt: String? = null)

@Serializable
data class EstimatedDeliveryDto(val minMinutes: Int, val maxMinutes: Int)

@Serializable
data class OrderPaymentDto(val label: String, val type: String = "")

@Serializable
data class CourierDto(val name: String, val vehicle: String)

@Serializable
data class OrderItemDto(
    val name: String,
    val restaurant: String = "",
    val imageHue: Float,
    val quantity: Int,
    val total: Double,
)

@Serializable
data class OrderAmountsDto(
    val subtotal: Double,
    val deliveryFee: Double,
    val discount: Double,
    val total: Double,
    val couponCode: String? = null,
)

// Item de GET /orders (Meus pedidos).
@Serializable
data class OrderListItemDto(
    val id: Int,
    val code: String,
    val status: String,
    val statusLabel: String,
    val createdAt: String,
    val total: Double,
    val itemCount: Int,
    val itemsSummary: String,
    val thumbnail: ThumbnailDto? = null,
)

@Serializable
data class ThumbnailDto(val imageHue: Float)
