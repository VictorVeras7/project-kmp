package org.example.project.data

data class DeliveryAddress(val label: String, val line: String, val city: String) {
    // Versão curta para o cabeçalho da Home (sem complemento).
    val short: String get() = line.substringBefore(" · ")
}

enum class PaymentMethod(val label: String, val hint: String) {
    Pix("PIX", "Aprovação imediata"),
    Credit("Cartão de crédito", "Visa •••• 4410"),
    Debit("Cartão de débito", "Nubank •••• 8802"),
    Cash("Dinheiro", "Troco para R$ 100,00");

    val needsCard: Boolean get() = this == Credit || this == Debit
}

val SampleAddresses = listOf(
    DeliveryAddress("Casa", "Rua das Palmeiras, 128 · apto 42", "Pinheiros · São Paulo"),
    DeliveryAddress("Trabalho", "Av. Faria Lima, 3400 · 9º andar", "Itaim Bibi · São Paulo"),
)

// Monta o endereço digitado no formulário; rua e bairro são obrigatórios.
fun newAddress(street: String, complement: String, district: String): DeliveryAddress? {
    if (street.isBlank() || district.isBlank()) return null
    val line = if (complement.isBlank()) street.trim() else "${street.trim()} · ${complement.trim()}"
    return DeliveryAddress("Novo endereço", line, district.trim())
}
