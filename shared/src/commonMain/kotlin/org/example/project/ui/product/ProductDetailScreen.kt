package org.example.project.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.DeliveryFee
import org.example.project.data.FreeDeliveryFrom
import org.example.project.data.Product
import org.example.project.data.formatMoney
import org.example.project.data.formatRating
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons

private val HeroHeight = 296.dp
private val SheetOverlap = 22.dp
private val RatingColor = Color(0xFFF5A623)
private val HeroIconColor = Color(0xFF1F1F1F)

@Composable
fun ProductDetailScreen(
    product: Product,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: (quantity: Int, addonIds: Set<String>, notes: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                ProductHero(product, isFavorite, onBack, onToggleFavorite)
                Column(
                    modifier = Modifier
                        .padding(top = HeroHeight - SheetOverlap)
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                        )
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ProductTitle(product)
                    ProductStats(product)
                    TextSection(
                        title = "Descrição",
                        body = "${product.description}. ${product.ingredients.substringBefore('.')}.",
                        fontSize = 13.5f,
                    )
                    TextSection(title = "Ingredientes", body = product.ingredients, fontSize = 13f)
                }
            }
        }
    }
}

@Composable
private fun ProductHero(
    product: Product,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
            .background(Color.hsv(product.hue, 0.3f, 0.97f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            HeroButton(icon = RangoIcons.ArrowBack, tint = HeroIconColor, onClick = onBack)
            HeroButton(
                icon = if (isFavorite) RangoIcons.HeartFilled else RangoIcons.Heart,
                tint = if (isFavorite) RangoColors.Error else HeroIconColor,
                onClick = onToggleFavorite,
            )
        }
    }
}

@Composable
private fun HeroButton(icon: ImageVector, tint: Color, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(40.dp)
            .shadow(6.dp, shape)
            .background(Color.White.copy(alpha = 0.92f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(21.dp))
    }
}

@Composable
private fun ProductTitle(product: Product) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                product.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 23.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
            )
            Column(horizontalAlignment = Alignment.End) {
                if (product.oldPrice != null) {
                    Text(
                        formatMoney(product.oldPrice),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }
                Text(
                    formatMoney(product.price),
                    color = RangoColors.Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                )
            }
        }
        Text(
            "${product.restaurant} · ${product.category}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ProductStats(product: Product) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatBox(RangoIcons.Star, RatingColor, formatRating(product.rating), "avaliação", Modifier.weight(1f))
        StatBox(
            RangoIcons.Clock,
            RangoColors.Primary,
            "${product.minMinutes}-${product.maxMinutes}",
            "minutos",
            Modifier.weight(1f),
        )
        StatBox(
            RangoIcons.Bike,
            RangoColors.Primary,
            if (product.price >= FreeDeliveryFrom) "Grátis" else formatMoney(DeliveryFee),
            "entrega",
            Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatBox(icon: ImageVector, tint: Color, value: String, label: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(15.dp)
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(horizontal = 6.dp, vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(19.dp))
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TextSection(title: String, body: String, fontSize: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        SectionTitle(title)
        Text(
            body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.55f).sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
}
