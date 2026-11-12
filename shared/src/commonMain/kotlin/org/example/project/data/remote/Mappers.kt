package org.example.project.data.remote

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.Addon
import org.example.project.data.Cart
import org.example.project.data.CartItem
import org.example.project.data.CartTotals
import org.example.project.data.Courier
import org.example.project.data.DeliveryAddress
import org.example.project.data.Order
import org.example.project.data.OrderDetail
import org.example.project.data.OrderLine
import org.example.project.data.OrderStatus
import org.example.project.data.OrderStep
import org.example.project.data.OrderSummary
import org.example.project.data.PaymentMethod
import org.example.project.data.PaymentOption
import org.example.project.data.Product
import org.example.project.data.ProductTag
import kotlin.time.Instant

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

private fun orderStatus(status: String) = when (status) {
    "live" -> OrderStatus.Live
    "cancel" -> OrderStatus.Cancelled
    else -> OrderStatus.Done
}

fun OrderListItemDto.toSummary() = OrderSummary(
    id = id,
    code = code,
    date = orderDate(createdAt),
    status = orderStatus(status),
    statusLabel = statusLabel,
    itemCount = itemCount,
    total = total,
    itemsSummary = itemsSummary,
    hue = thumbnail?.imageHue ?: 0f,
)

fun OrderDto.toDetail() = OrderDetail(
    id = id,
    code = code,
    date = orderDate(createdAt),
    status = orderStatus(status),
    statusLabel = statusLabel,
    items = items.map { OrderLine(it.name, it.restaurant, it.quantity, it.total, it.imageHue) },
    address = address?.line.orEmpty(),
    payment = MethodsByType[payment.type],
    paymentLabel = payment.label,
    totals = amounts?.let { CartTotals(it.subtotal, it.deliveryFee, it.discount) } ?: CartTotals(total, 0.0, 0.0),
    couponCode = amounts?.couponCode,
)

// "2026-09-21T19:42:00.000Z" -> "16:42" no fuso do aparelho.
private fun localTime(iso: String): String? = runCatching {
    val time = Instant.parse(iso).toLocalDateTime(TimeZone.currentSystemDefault())
    "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}.getOrNull()
