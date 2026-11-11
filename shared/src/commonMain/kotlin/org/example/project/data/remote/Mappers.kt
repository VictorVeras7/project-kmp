package org.example.project.data.remote

import org.example.project.data.Addon
import org.example.project.data.Cart
import org.example.project.data.CartItem
import org.example.project.data.CartTotals
import org.example.project.data.Courier
import org.example.project.data.DeliveryAddress
import org.example.project.data.Order
import org.example.project.data.OrderStep
import org.example.project.data.PaymentMethod
import org.example.project.data.PaymentOption
import org.example.project.data.Product
import org.example.project.data.ProductTag

// Converte as respostas da API nos modelos usados pelas telas.

private val TagsByKey = mapOf(
    "offers" to ProductTag.Offer,
    "popular" to ProductTag.Popular,
    "recommended" to ProductTag.Recommended,
    "fast" to ProductTag.Fast,
)

fun ProductDto.toProduct() = Product(
    id = id,
    name = name,
    restaurant = restaurant,
    category = category.name,
    description = description,
    ingredients = ingredients.orEmpty(),
    price = price,
    oldPrice = oldPrice,
    rating = rating,
    minMinutes = deliveryTime.min,
    maxMinutes = deliveryTime.max,
    hue = imageHue,
    tags = tags.mapNotNull { TagsByKey[it] }.toSet(),
)

fun AddonDto.toAddon() = Addon(id, name, price)

fun CartDto.toCart() = Cart(
    items = items.map { line ->
        CartItem(
            productId = line.product.id,
            quantity = line.quantity,
            addonIds = line.addons.map { it.id }.toSet(),
            notes = line.notes,
            unitPrice = line.unitPrice,
            lineId = line.id,
        )
    },
    couponCode = coupon?.takeIf { it.applied }?.code,
    totals = CartTotals(summary.subtotal, summary.deliveryFee, summary.discount),
)

fun AddressDto.toAddress() = DeliveryAddress(label = label, line = line, city = cityLine, id = id)

private val MethodsByType = mapOf(
    "pix" to PaymentMethod.Pix,
    "credit" to PaymentMethod.Credit,
    "debit" to PaymentMethod.Debit,
    "cash" to PaymentMethod.Cash,
)

fun PaymentMethodDto.toOption(): PaymentOption? =
    MethodsByType[type]?.let { PaymentOption(id, it, label, detail) }

fun OrderDto.toOrder(): Order {
    val currentStep = OrderStep.entries.getOrElse(step) { OrderStep.Received }
    val delivered = currentStep == OrderStep.Delivered
    val eta = when {
        status == "cancel" -> "Cancelado"
        delivered -> "Entregue"
        estimatedDelivery != null -> "${estimatedDelivery.minMinutes}-${estimatedDelivery.maxMinutes} min"
        else -> ""
    }
    return Order(
        id = id,
        code = code,
        itemCount = itemCount,
        total = total,
        paymentLabel = payment.label,
        step = currentStep,
        isLive = status == "live",
        hint = hint,
        eta = eta,
        stepTimes = timeline.mapNotNull { item ->
            val time = item.reachedAt?.let(::localTime) ?: return@mapNotNull null
            OrderStep.entries.getOrNull(item.step)?.let { it to time }
        }.toMap(),
        courier = courier?.let { Courier(it.name, it.vehicle) },
    )
}
