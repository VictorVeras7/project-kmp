package org.example.project.data

data class CartItem(
    val productId: String,
    val quantity: Int,
    val addonIds: Set<String>,
    val notes: String,
    val unitPrice: Double,
    // Id da linha no servidor, usado para alterar a quantidade ou remover.
    val lineId: Int = 0,
) {
    // Mesmo produto com os mesmos adicionais vira uma linha só.
    val key: String get() = cartKey(productId, addonIds)
}

data class CartTotals(
    val subtotal: Double,
    val delivery: Double,
    val discount: Double,
) {
    val total: Double get() = subtotal + delivery - discount
}

// Carrinho como o servidor devolve: linhas, cupom aplicado e totais já calculados.
data class Cart(
    val items: List<CartItem> = emptyList(),
    val couponCode: String? = null,
    val totals: CartTotals = CartTotals(0.0, 0.0, 0.0),
) {
    val couponApplied: Boolean get() = couponCode != null
}

const val CouponCode = "RANGO10"
const val CouponDiscount = 0.10
const val CartFreeDeliveryFrom = 60.0

fun cartKey(productId: String, addonIds: Set<String>): String =
    productId + "|" + addonIds.sorted().joinToString(",")

// Montagem local do carrinho, usada nos previews (no app o carrinho vem do servidor).
fun List<CartItem>.add(
    product: Product,
    quantity: Int,
    addonIds: Set<String> = emptySet(),
    notes: String = "",
    addons: List<Addon> = SampleAddons,
): List<CartItem> {
    val key = cartKey(product.id, addonIds)
    if (any { it.key == key }) {
        return map { if (it.key == key) it.copy(quantity = it.quantity + quantity) else it }
    }
    val unitPrice = product.price + addons.filter { it.id in addonIds }.sumOf { it.price }
    return this + CartItem(product.id, quantity, addonIds, notes, unitPrice)
}

fun List<CartItem>.itemCount(): Int = sumOf { it.quantity }

fun List<CartItem>.totals(couponApplied: Boolean): CartTotals {
    val subtotal = sumOf { it.unitPrice * it.quantity }
    val delivery = if (subtotal == 0.0 || subtotal >= CartFreeDeliveryFrom) 0.0 else DeliveryFee
    val discount = if (couponApplied) subtotal * CouponDiscount else 0.0
    return CartTotals(subtotal, delivery, discount)
}
