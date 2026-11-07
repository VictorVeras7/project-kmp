package org.example.project.data

// O protótipo já começa com 4 pedidos no histórico, então o primeiro novo é RG-10528.
private const val FirstOrderNumber = 10528
private const val OrderNumberStep = 7

const val OrderEta = "25-35 min"

data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val address: DeliveryAddress,
    val payment: PaymentMethod,
) {
    val itemsLabel: String
        get() {
            val count = items.itemCount()
            return if (count == 1) "1 item" else "$count itens"
        }
}

fun orderId(previousOrders: Int): String = "RG-${FirstOrderNumber + previousOrders * OrderNumberStep}"
