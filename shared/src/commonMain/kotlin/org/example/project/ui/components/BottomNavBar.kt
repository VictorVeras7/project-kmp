package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

// Abas da barra inferior, na ordem do protótipo.
enum class MainTab(val label: String, val icon: ImageVector) {
    Home("Início", RangoIcons.Home),
    Search("Buscar", RangoIcons.Search),
    Orders("Pedidos", RangoIcons.Receipt),
    Favorites("Favoritos", RangoIcons.Heart),
    Profile("Perfil", RangoIcons.User),
}

@Composable
fun BottomNavBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
    // Número no canto do ícone (pedidos em andamento, favoritos); sem número, sem badge.
    badges: Map<MainTab, Int> = emptyMap(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)),
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 6.dp, end = 6.dp, top = 8.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            MainTab.entries.forEach { tab ->
                NavItem(
                    tab = tab,
                    active = tab == selected,
                    badge = badges[tab]?.takeIf { it > 0 },
                    onClick = { onSelect(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NavItem(tab: MainTab, active: Boolean, badge: Int?, onClick: () -> Unit, modifier: Modifier) {
    val color = if (active) RangoColors.Primary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            val icon = if (tab == MainTab.Favorites && active) RangoIcons.HeartFilled else tab.icon
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(23.dp))
            Text(tab.label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .offset(x = 13.dp)
                    .defaultMinSize(minWidth = 17.dp)
                    .height(17.dp)
                    .background(RangoColors.Primary, RoundedCornerShape(9.dp))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(badge.toString(), color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Preview
@Composable
private fun BottomNavBarPreview() {
    RangoTheme(darkTheme = false) {
        BottomNavBar(selected = MainTab.Orders, onSelect = {}, badges = mapOf(MainTab.Orders to 1, MainTab.Favorites to 3))
    }
}

@Preview
@Composable
private fun BottomNavBarDarkPreview() {
    RangoTheme(darkTheme = true) {
        BottomNavBar(selected = MainTab.Home, onSelect = {}, badges = mapOf(MainTab.Favorites to 2))
    }
}
