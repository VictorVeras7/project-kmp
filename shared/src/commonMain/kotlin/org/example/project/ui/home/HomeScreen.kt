package org.example.project.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.Product
import org.example.project.data.ProductCategories
import org.example.project.data.SampleProducts
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons

@Composable
fun HomeScreen(
    userName: String,
    address: String,
    cartCount: Int,
    favoriteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onOpenProduct: (String) -> Unit,
    onOpenCart: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier,
    products: List<Product> = SampleProducts,
) {
    var category by remember { mutableStateOf(ProductCategories.first()) }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeader(userName, address, cartCount, onOpenNotifications, onOpenCart)
            SearchBar(onClick = onOpenSearch)
            CategoryChips(selected = category, onSelect = { category = it })
            if (category == ProductCategories.first()) PromoBanner()
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    address: String,
    cartCount: Int,
    onOpenNotifications: () -> Unit,
    onOpenCart: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                userName.take(2).uppercase(),
                color = RangoColors.Primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Olá, ${userName.substringBefore(' ')}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(RangoIcons.Pin, null, tint = RangoColors.Primary, modifier = Modifier.size(15.dp))
                Text(
                    address,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Icon(RangoIcons.ChevronDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }
        }
        HeaderIconButton(icon = RangoIcons.Bell, filled = false, badge = null, onClick = onOpenNotifications)
        HeaderIconButton(
            icon = RangoIcons.Bag,
            filled = true,
            badge = cartCount.takeIf { it > 0 }?.toString(),
            onClick = onOpenCart,
        )
    }
}

@Composable
private fun HeaderIconButton(icon: ImageVector, filled: Boolean, badge: String?, onClick: () -> Unit) {
    Box(modifier = Modifier.size(42.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (filled) RangoColors.Primary else MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(15.dp),
                )
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(15.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (filled) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(21.dp),
            )
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .background(RangoColors.Error, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SearchBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(RangoIcons.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(19.dp))
        Text(
            "O que você quer comer hoje?",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CategoryChips(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ProductCategories.forEach { name ->
            val on = name == selected
            val shape = RoundedCornerShape(13.dp)
            Text(
                name,
                color = if (on) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(if (on) RangoColors.Primary else MaterialTheme.colorScheme.surface, shape)
                    .border(1.dp, if (on) RangoColors.Primary else MaterialTheme.colorScheme.outline, shape)
                    .clickable { onSelect(name) }
                    .padding(horizontal = 15.dp, vertical = 9.dp),
            )
        }
    }
}

@Composable
private fun PromoBanner() {
    Column(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(RangoColors.Primary, RangoColors.PrimarySoft)),
                RoundedCornerShape(24.dp),
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text("SÓ HOJE", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("20% OFF acima\nde R$ 40", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(
            "RANGO20",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}
