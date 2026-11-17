package org.example.project.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.Product
import org.example.project.data.SampleProducts
import org.example.project.data.formatMoney
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

@Composable
fun FavoritesScreen(
    favorites: List<Product>,
    loading: Boolean,
    onOpenProduct: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onExplore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
        ) {
            FavoritesHeader(count = favorites.size)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when {
                    favorites.isNotEmpty() -> favorites.forEach { product ->
                        FavoriteCard(
                            product = product,
                            onOpen = { onOpenProduct(product.id) },
                            onRemove = { onToggleFavorite(product.id) },
                            onAdd = { onAddToCart(product.id) },
                        )
                    }
                    loading -> Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RangoColors.Primary)
                    }
                    else -> EmptyFavorites(onExplore = onExplore)
                }
            }
        }
    }
}

private fun savedLabel(count: Int): String = if (count == 1) "1 item salvo" else "$count itens salvos"

@Composable
private fun FavoritesHeader(count: Int) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Favoritos",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.6).sp,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                savedLabel(count),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alignByBaseline(),
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
    }
}

// Card do favorito: foto, nome com o coração para remover, restaurante e categoria, preço e "Adicionar".
@Composable
private fun FavoriteCard(product: Product, onOpen: () -> Unit, onRemove: () -> Unit, onAdd: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Box(
            Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.hsv(product.hue, 0.3f, 0.97f))
                .clickable(onClick = onOpen),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    product.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).clickable(onClick = onOpen),
                )
                Icon(
                    RangoIcons.HeartFilled,
                    contentDescription = "Remover dos favoritos",
                    tint = RangoColors.Error,
                    modifier = Modifier.size(21.dp).clip(RoundedCornerShape(6.dp)).clickable(onClick = onRemove),
                )
            }
            Text(
                "${product.restaurant} · ${product.category}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(formatMoney(product.price), color = RangoColors.Primary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Button(
                    onClick = onAdd,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Icon(RangoIcons.CartPlus, contentDescription = null, modifier = Modifier.size(17.dp))
                    Text("Adicionar", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyFavorites(onExplore: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        val borderColor = MaterialTheme.colorScheme.outline
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))
                .drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        cornerRadius = CornerRadius(32.dp.toPx()),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
                        ),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(RangoIcons.Heart, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(42.dp))
        }
        Text("Nenhum favorito ainda", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.5.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "Toque no coração dos pratos que você ama para encontrá-los rápido aqui.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            lineHeight = 19.5.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 250.dp),
        )
        Button(
            onClick = onExplore,
            modifier = Modifier.padding(top = 4.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text("Explorar produtos", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
        }
    }
}
