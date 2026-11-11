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
    val itemsLabel: String get() = if (itemCount == 1) "1 item" else "$itemCount itens"
}

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
