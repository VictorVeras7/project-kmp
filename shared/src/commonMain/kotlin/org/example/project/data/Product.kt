package org.example.project.data

enum class ProductTag { Offer, Popular, Recommended, Fast }

data class Product(
    val id: String,
    val name: String,
    val restaurant: String,
    val category: String,
    val description: String,
    val ingredients: String,
    val price: Double,
    val oldPrice: Double?,
    val rating: Double,
    val minMinutes: Int,
    val maxMinutes: Int,
    val hue: Float,
    val tags: Set<ProductTag>,
)

data class Addon(val id: String, val name: String, val price: Double)

val SampleAddons = listOf(
    Addon("a1", "Bacon extra", 4.0),
    Addon("a2", "Cheddar extra", 3.5),
    Addon("a3", "Molho da casa", 2.0),
    Addon("a4", "Refrigerante 350ml", 7.0),
)

const val DeliveryFee = 7.9
const val FreeDeliveryFrom = 50.0

val ProductCategories = listOf("Todos", "Hambúrguer", "Pizza", "Massas", "Brasileira", "Sobremesas", "Bebidas")

val SampleProducts = listOf(
    Product("p1", "Burger duplo cheddar", "Casa do Burger", "Hambúrguer", "Dois blends de 120g com cheddar inglês", "Pão brioche, 2 blends de 120g, cheddar inglês, cebola caramelizada, molho da casa.", 34.9, 42.9, 4.8, 25, 35, 48f, setOf(ProductTag.Offer, ProductTag.Popular)),
    Product("p2", "Pizza margherita", "Forno di Napoli", "Pizza", "Massa de fermentação natural, 8 fatias", "Molho de tomate San Marzano, muçarela de búfala, manjericão fresco, azeite.", 45.0, null, 4.7, 30, 40, 28f, setOf(ProductTag.Popular, ProductTag.Recommended)),
    Product("p3", "Fettuccine alfredo", "Cantina Bella", "Massas", "Massa fresca ao molho cremoso", "Fettuccine fresco, creme de leite fresco, parmesão 24 meses, noz-moscada.", 39.9, null, 4.6, 35, 45, 82f, setOf(ProductTag.Recommended)),
    Product("p4", "Feijoada completa", "Tempero Mineiro", "Brasileira", "Serve 2 · acompanhamentos inclusos", "Feijão preto, carnes selecionadas, arroz, couve, farofa, laranja e vinagrete.", 52.0, 59.9, 4.9, 40, 50, 62f, setOf(ProductTag.Offer, ProductTag.Recommended)),
    Product("p5", "Brownie com sorvete", "Doce Ponto", "Sobremesas", "Brownie quente e sorvete de creme", "Chocolate 70%, castanhas, sorvete de creme, calda quente de chocolate.", 18.5, null, 4.9, 15, 25, 35f, setOf(ProductTag.Popular, ProductTag.Fast)),
    Product("p6", "Suco verde detox", "Verde Vivo", "Bebidas", "500ml · sem açúcar", "Couve, maçã verde, limão, gengibre e hortelã.", 12.0, null, 4.4, 10, 20, 140f, setOf(ProductTag.Fast)),
    Product("p7", "Smash burger bacon", "Casa do Burger", "Hambúrguer", "Smash de 100g com bacon crocante", "Pão de batata, smash 100g, bacon, queijo prato, maionese defumada.", 29.9, 35.9, 4.7, 20, 30, 42f, setOf(ProductTag.Offer, ProductTag.Popular, ProductTag.Fast)),
    Product("p8", "Pizza pepperoni", "Forno di Napoli", "Pizza", "Pepperoni italiano fatiado na hora", "Molho de tomate, muçarela, pepperoni italiano, orégano.", 49.9, null, 4.8, 30, 40, 20f, setOf(ProductTag.Recommended)),
    Product("p9", "Lasanha à bolonhesa", "Cantina Bella", "Massas", "Camadas de massa fresca e ragu", "Massa fresca, ragu de carne, molho branco, parmesão gratinado.", 42.0, null, 4.5, 35, 45, 72f, emptySet()),
    Product("p10", "Moqueca de camarão", "Tempero Mineiro", "Brasileira", "Serve 2 · arroz e pirão inclusos", "Camarão rosa, leite de coco, dendê, pimentões, coentro, arroz e pirão.", 68.0, null, 4.9, 45, 60, 32f, setOf(ProductTag.Recommended)),
    Product("p11", "Petit gâteau", "Doce Ponto", "Sobremesas", "Recheio cremoso de chocolate", "Chocolate meio amargo, manteiga, ovos, sorvete de baunilha.", 22.0, null, 4.8, 20, 30, 16f, setOf(ProductTag.Recommended)),
    Product("p12", "Limonada suíça", "Verde Vivo", "Bebidas", "400ml · feita na hora", "Limão taiti, leite condensado, gelo.", 9.9, null, 4.6, 10, 15, 112f, setOf(ProductTag.Fast)),
    Product("p13", "Açaí 500ml", "Doce Ponto", "Sobremesas", "Com banana, granola e mel", "Açaí batido, banana, granola artesanal, mel e leite em pó.", 24.9, 29.9, 4.7, 15, 25, 305f, setOf(ProductTag.Offer, ProductTag.Fast)),
    Product("p14", "Combo burger + fritas", "Casa do Burger", "Hambúrguer", "Burger, fritas grandes e bebida", "Burger 180g, fritas rústicas 300g, refrigerante 350ml.", 44.9, null, 4.6, 25, 35, 52f, setOf(ProductTag.Popular)),
)

fun formatMoney(value: Double): String {
    val cents = (value * 100).toLong()
    return "R$ ${cents / 100},${(cents % 100).toString().padStart(2, '0')}"
}

fun formatRating(value: Double): String = value.toString().replace('.', ',')
