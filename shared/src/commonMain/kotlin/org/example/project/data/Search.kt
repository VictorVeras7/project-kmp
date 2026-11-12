package org.example.project.data

// Ordenação da busca: rótulo da folha de filtros, rótulo curto (chip e topo dos resultados) e valor da API.
enum class SearchSort(val label: String, val shortLabel: String, val apiValue: String) {
    Relevance("Relevância", "Relevância", "relevance"),
    PriceAsc("Preço · crescente", "Menor preço", "price_asc"),
    PriceDesc("Preço · decrescente", "Maior preço", "price_desc"),
    Rating("Melhor avaliados", "Melhor avaliados", "rating"),
}

const val SearchMinPrice = 10
const val SearchMaxPrice = 80
const val SearchPriceStep = 5
const val AnyDeliveryTime = 60

// Opções dos chips da folha de filtros, como no protótipo.
val RatingOptions = listOf(0.0 to "Todas", 4.0 to "4,0+", 4.5 to "4,5+", 4.8 to "4,8+")
val DeliveryTimeOptions = listOf(AnyDeliveryTime to "Qualquer", 30 to "até 30 min", 25 to "até 25 min")

// Valores padrão = sem filtro. Cada campo diferente do padrão conta no número do botão de filtros.
data class SearchFilters(
    val category: String = ProductCategories.first(),
    val maxPrice: Int = SearchMaxPrice,
    val minRating: Double = 0.0,
    val maxTime: Int = AnyDeliveryTime,
    val sort: SearchSort = SearchSort.Relevance,
) {
    val activeCount: Int
        get() = listOf(
            category != ProductCategories.first(),
            maxPrice != SearchMaxPrice,
            minRating != 0.0,
            maxTime != AnyDeliveryTime,
            sort != SearchSort.Relevance,
        ).count { it }

    val isDefault: Boolean
        get() = activeCount == 0
}

// Sugestão da busca: nome do produto e o resumo embaixo (restaurante, nota e preço).
data class SearchSuggestion(val label: String, val hint: String)

fun resultsLabel(count: Int): String = if (count == 1) "1 resultado" else "$count resultados"
