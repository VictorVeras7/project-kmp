package org.example.project.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.DeliveryTimeOptions
import org.example.project.data.ProductCategories
import org.example.project.data.RatingOptions
import org.example.project.data.SearchFilters
import org.example.project.data.SearchMaxPrice
import org.example.project.data.SearchMinPrice
import org.example.project.data.SearchPriceStep
import org.example.project.data.SearchSort
import org.example.project.data.formatMoney
import org.example.project.ui.checkout.RadioDot
import org.example.project.ui.checkout.SelectableRow
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import kotlin.math.roundToInt

// Folha "Filtros": edita um rascunho; só vale ao tocar em "Aplicar filtros".
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterSheet(
    initial: SearchFilters,
    onDismiss: () -> Unit,
    onApply: (SearchFilters) -> Unit,
) {
    var draft by remember { mutableStateOf(initial) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.45f),
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 12.dp)
                    .width(44.dp)
                    .height(4.5.dp)
                    .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(3.dp)),
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Filtros",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(RangoIcons.Close, contentDescription = "Fechar", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(19.dp))
                }
            }

            Column(
                modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                FilterGroup("Categoria") {
                    ChipFlow {
                        ProductCategories.forEach { name ->
                            FilterChip(name, draft.category == name) { draft = draft.copy(category = name) }
                        }
                    }
                }
                PriceGroup(draft.maxPrice) { draft = draft.copy(maxPrice = it) }
                FilterGroup("Avaliação mínima") {
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        RatingOptions.forEach { (value, label) ->
                            FilterChip(label, draft.minRating == value) { draft = draft.copy(minRating = value) }
                        }
                    }
                }
                FilterGroup("Tempo de entrega") {
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        DeliveryTimeOptions.forEach { (value, label) ->
                            FilterChip(label, draft.maxTime == value) { draft = draft.copy(maxTime = value) }
                        }
                    }
                }
                FilterGroup("Ordenar por", spacing = 7.dp) {
                    SearchSort.entries.forEach { sort ->
                        SelectableRow(selected = draft.sort == sort, onClick = { draft = draft.copy(sort = sort) }) {
                            RadioDot(draft.sort == sort)
                            Text(
                                sort.label,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { draft = SearchFilters() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Text("Limpar filtros", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onApply(draft) },
                    modifier = Modifier.weight(1.4f).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text("Aplicar filtros", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FilterGroup(title: String, spacing: Dp = 9.dp, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
        GroupTitle(title)
        content()
    }
}

@Composable
private fun GroupTitle(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(content: @Composable () -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        content()
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Text(
        label,
        color = if (selected) RangoColors.Primary else MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.5.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(shape)
            .background(if (selected) RangoColors.Primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant, shape)
            .border(1.dp, if (selected) RangoColors.Primary else MaterialTheme.colorScheme.outline, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
    )
}
