package org.example.project.data

// Intervalo entre as consultas ao servidor enquanto o pedido está em andamento.
const val OrderPollIntervalMs = 5000L

// Etapas do pedido, na ordem (mesmos índices 0-4 da API).
enum class OrderStep(val label: String) {
    Received("Pedido recebido"),
    Confirmed("Pedido confirmado"),
    Preparing("Em preparação"),
    OnTheWay("Saiu para entrega"),
    Delivered("Entregue"),
}

data class Courier(val name: String, val vehicle: String)

data class Order(
    val id: Int,
    val code: String,
    val itemCount: Int,
    val total: Double,
    val paymentLabel: String,
    val step: OrderStep,
    val isLive: Boolean,
    val hint: String,
    val eta: String,
    // Horário (HH:mm) em que cada etapa concluída foi alcançada.
    val stepTimes: Map<OrderStep, String> = emptyMap(),
    val courier: Courier? = null,
) {
    val itemsLabel: String get() = itemsLabel(itemCount)
}

fun itemsLabel(count: Int): String = if (count == 1) "1 item" else "$count itens"

// Situação do pedido no histórico: em andamento, entregue ou cancelado.
enum class OrderStatus { Live, Done, Cancelled }

// Pedido na lista de "Meus pedidos".
data class OrderSummary(
    val id: Int,
    val code: String,
    val date: String,
    val status: OrderStatus,
    val statusLabel: String,
    val itemCount: Int,
    val total: Double,
    val itemsSummary: String,
    val hue: Float,
)

data class OrderLine(val name: String, val restaurant: String, val quantity: Int, val total: Double, val hue: Float)

// Pedido completo, para a tela de detalhes.
data class OrderDetail(
    val id: Int,
    val code: String,
    val date: String,
    val status: OrderStatus,
    val statusLabel: String,
    val items: List<OrderLine>,
    val address: String,
    val payment: PaymentMethod?,
    val paymentLabel: String,
    val totals: CartTotals,
    val couponCode: String?,
)

val SampleOrder = Order(
    id = 10528,
    code = "RG-10528",
    itemCount = 3,
    total = 74.7,
    paymentLabel = "PIX",
    step = OrderStep.Preparing,
    isLive = true,
    hint = "Seu pedido está sendo preparado",
    eta = "25-35 min",
    stepTimes = mapOf(OrderStep.Received to "19:42", OrderStep.Confirmed to "19:44", OrderStep.Preparing to "19:51"),
    courier = Courier("Juliana Prado", "Yamaha Factor 150"),
)

val SampleOrderHistory = listOf(
    OrderSummary(10482, "RG-10482", "Hoje · 19:42", OrderStatus.Live, "Em preparação", 3, 71.8, "1× Burger duplo cheddar, 2× Limonada suíça", 48f),
    OrderSummary(10391, "RG-10391", "12 ago · 20:15", OrderStatus.Done, "Entregue", 1, 52.9, "1× Pizza margherita", 28f),
    OrderSummary(10298, "RG-10298", "28 jul · 21:30", OrderStatus.Cancelled, "Cancelado", 1, 37.8, "1× Poke de salmão", 190f),
)

val SampleOrderDetail = OrderDetail(
    id = 10391,
    code = "RG-10391",
    date = "12 ago · 20:15",
    status = OrderStatus.Done,
    statusLabel = "Entregue",
    items = listOf(
        OrderLine("Lasanha à bolonhesa", "Cantina Bella", 2, 84.0, 72f),
        OrderLine("Burger duplo cheddar", "Casa do Burger", 1, 38.9, 48f),
    ),
    address = "Rua das Palmeiras, 128 · apto 42",
    payment = PaymentMethod.Credit,
    paymentLabel = "Cartão de crédito",
    totals = CartTotals(122.9, 0.0, 12.29),
    couponCode = "RANGO10",
)
