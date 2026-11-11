package org.example.project.data

data class DeliveryAddress(
    val label: String,
    val line: String,
    val city: String,
    val id: Int = 0,
) {
    // Versão curta para o cabeçalho da Home (sem complemento).
    val short: String get() = line.substringBefore(" · ")
}

enum class PaymentMethod {
    Pix, Credit, Debit, Cash;

    val needsCard: Boolean get() = this == Credit || this == Debit
}

// Forma de pagamento cadastrada no servidor: id para o pedido e textos para exibir.
data class PaymentOption(val id: Int, val method: PaymentMethod, val label: String, val hint: String)

val SampleAddresses = listOf(
    DeliveryAddress("Casa", "Rua das Palmeiras, 128 · apto 42", "Pinheiros · São Paulo", id = 1),
    DeliveryAddress("Trabalho", "Av. Faria Lima, 3400 · 9º andar", "Itaim Bibi · São Paulo", id = 2),
)

val SamplePayments = listOf(
    PaymentOption(1, PaymentMethod.Pix, "PIX", "Aprovação imediata"),
    PaymentOption(2, PaymentMethod.Credit, "Cartão de crédito", "Visa •••• 4410"),
    PaymentOption(3, PaymentMethod.Debit, "Cartão de débito", "Nubank •••• 8802"),
    PaymentOption(4, PaymentMethod.Cash, "Dinheiro", "Pagamento na entrega"),
)

// Dados do formulário "Novo endereço", já separados como a API espera.
data class AddressForm(
    val cep: String,
    val street: String,
    val complement: String?,
    val neighborhood: String,
    val city: String,
)

// O formulário tem um campo só para "Bairro e cidade"; separa por vírgula, "·" ou " - ".
fun splitDistrict(value: String): Pair<String, String>? {
    val parts = value.split(",", "·", " - ").map { it.trim() }.filter { it.isNotEmpty() }
    if (parts.size < 2) return null
    return parts.first() to parts.drop(1).joinToString(" ")
}

// Valida o formulário. CEP com 8 dígitos, rua, bairro e cidade são obrigatórios.
fun addressForm(cep: String, street: String, complement: String, district: String): AddressForm? {
    val cepDigits = cep.filter { it.isDigit() }
    val (neighborhood, city) = splitDistrict(district) ?: return null
    if (cepDigits.length != 8 || street.isBlank()) return null
    return AddressForm(cepDigits, street.trim(), complement.trim().ifEmpty { null }, neighborhood, city)
}
