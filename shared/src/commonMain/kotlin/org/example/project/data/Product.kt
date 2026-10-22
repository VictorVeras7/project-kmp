package org.example.project.data

enum class ProductTag { Offer, Popular, Recommended, Fast }

data class Product(
    val id: String,
    val name: String,
    val restaurant: String,
    val category: String,
    val description: String,
    val price: Double,
    val oldPrice: Double?,
    val rating: Double,
    val minMinutes: Int,
    val maxMinutes: Int,
    val hue: Float,
    val tags: Set<ProductTag>,
)

val ProductCategories = listOf("Todos", "Hambúrguer", "Pizza", "Massas", "Brasileira", "Sobremesas", "Bebidas")
