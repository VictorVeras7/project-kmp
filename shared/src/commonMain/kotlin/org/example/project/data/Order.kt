package org.example.project.data

// O protótipo já começa com 4 pedidos no histórico, então o primeiro novo é RG-10528.
private const val FirstOrderNumber = 10528
private const val OrderNumberStep = 7

const val OrderEta = "25-35 min"

// Intervalo entre as mudanças de status, simulando o restaurante e o entregador.
const val TrackingIntervalMs = 5000L

// Etapas do pedido, na ordem. O horário só aparece quando a etapa já foi concluída.
enum class OrderStep(val label: String, val hint: String, val time: String) {
    Received("Pedido recebido", "Aguardando o restaurante", "19:42"),
    Confirmed("Pedido confirmado", "O restaurante aceitou seu pedido", "19:44"),
    Preparing("Em preparação", "Seu pedido está sendo preparado", "19:51"),
    OnTheWay("Saiu para entrega", "Rafael está levando seu pedido", "20:06"),
    Delivered("Entregue", "Bom apetite!", "20:14");

    val next: OrderStep get() = entries.getOrElse(ordinal + 1) { this }
}

data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val address: DeliveryAddress,
    val payment: PaymentMethod,
    // Ao confirmar, o pagamento já foi aprovado, então começa em "Pedido confirmado".
    val step: OrderStep = OrderStep.Confirmed,
) {
    val itemsLabel: String
        get() {
            val count = items.itemCount()
            return if (count == 1) "1 item" else "$count itens"
        }

    val eta: String get() = if (step == OrderStep.Delivered) "Entregue" else OrderEta
}

fun orderId(previousOrders: Int): String = "RG-${FirstOrderNumber + previousOrders * OrderNumberStep}"
