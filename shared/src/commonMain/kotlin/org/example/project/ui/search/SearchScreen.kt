package org.example.project.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.AnyDeliveryTime
import org.example.project.data.Product
import org.example.project.data.ProductCategories
import org.example.project.data.SampleProducts
import org.example.project.data.SampleRecentSearches
import org.example.project.data.SampleSuggestions
import org.example.project.data.SearchFilters
import org.example.project.data.SearchMaxPrice
import org.example.project.data.SearchSort
import org.example.project.data.SearchSuggestion
import org.example.project.data.formatMoney
import org.example.project.data.formatRating
import org.example.project.data.resultsLabel
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

private val Star = Color(0xFFF5A623)

@Composable
fun SearchScreen(
    query: String,
    filters: SearchFilters,
    // null = termo vazio e sem filtros: mostra buscas recentes e sugestões.
    results: List<Product>?,
    searching: Boolean,
    recentSearches: List<String>,
    suggestions: List<SearchSuggestion>,
    favoriteIds: Set<String>,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onApplyFilters: (SearchFilters) -> Unit,
    onClearRecent: () -> Unit,
    onOpenProduct: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    modifier: Modifier = Modifier,
    // Avisa "Filtros aplicados." (só ao confirmar na folha, não ao remover um chip).
    onFiltersApplied: () -> Unit = {},
) {
    var showFilters by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
        ) {
            SearchHeader(
                query = query,
                filters = filters,
                onQueryChange = onQueryChange,
                onSubmit = onSubmit,
                onOpenFilters = { showFilters = true },
                onApplyFilters = onApplyFilters,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                when {
                    results == null -> IdleContent(
                        recentSearches = recentSearches,
                        suggestions = suggestions,
                        onPick = onQueryChange,
                        onClearRecent = onClearRecent,
                    )
                    results.isNotEmpty() -> Results(
                        results = results,
                        sort = filters.sort,
                        favoriteIds = favoriteIds,
                        onOpenProduct = onOpenProduct,
                        onToggleFavorite = onToggleFavorite,
                        onAddToCart = onAddToCart,
                    )
                    searching -> Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RangoColors.Primary)
                    }
                    else -> NoResults(onClearFilters = { onApplyFilters(SearchFilters()) })
                }
            }
        }
    }

    if (showFilters) {
        FilterSheet(
            initial = filters,
            onDismiss = { showFilters = false },
            onApply = {
                showFilters = false
                onApplyFilters(it)
                onFiltersApplied()
            },
        )
    }
}

@Composable
private fun SearchHeader(
    query: String,
    filters: SearchFilters,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onOpenFilters: () -> Unit,
    onApplyFilters: (SearchFilters) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier.padding(top = 6.dp, bottom = 13.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                SearchField(query, onQueryChange, onSubmit, Modifier.weight(1f))
                FilterButton(count = filters.activeCount, onClick = onOpenFilters)
            }
            if (!filters.isDefault) ActiveFilterChips(filters, onApplyFilters)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, onSubmit: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(15.dp)
    val focus = LocalFocusManager.current
    Row(
        modifier = modifier
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .border(1.5.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Icon(RangoIcons.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSubmit()
                focus.clearFocus()
            }),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { inner ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            "O que você quer comer hoje?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                    }
                    inner()
                }
            },
        )
        if (query.isNotEmpty()) {
            Icon(
                RangoIcons.Close,
                contentDescription = "Limpar busca",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(19.dp).clickable { onQueryChange("") },
            )
        }
    }
}

// Botão laranja de filtros, com o número de filtros ativos no canto.
@Composable
private fun FilterButton(count: Int, onClick: () -> Unit) {
    Box(modifier = Modifier.size(48.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .background(RangoColors.Primary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(RangoIcons.Tune, contentDescription = "Filtros", tint = Color.White, modifier = Modifier.size(22.dp))
        }
        if (count > 0) {
            val surface = MaterialTheme.colorScheme.surface
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .defaultMinSize(minWidth = 18.dp)
                    .height(18.dp)
                    .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(9.dp))
                    .border(2.dp, surface, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(count.toString(), color = surface, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

// Um chip por filtro ativo; tocar remove só aquele filtro.
@Composable
private fun ActiveFilterChips(filters: SearchFilters, onApply: (SearchFilters) -> Unit) {
    val defaults = SearchFilters()
    val chips = buildList {
        if (filters.category != defaults.category) add(filters.category to filters.copy(category = defaults.category))
        if (filters.maxPrice != SearchMaxPrice) add("até ${formatMoney(filters.maxPrice.toDouble())}" to filters.copy(maxPrice = SearchMaxPrice))
        if (filters.minRating != 0.0) add("${formatRating(filters.minRating)}+ estrelas" to filters.copy(minRating = 0.0))
        if (filters.maxTime != AnyDeliveryTime) add("até ${filters.maxTime} min" to filters.copy(maxTime = AnyDeliveryTime))
        if (filters.sort != SearchSort.Relevance) add(filters.sort.shortLabel to filters.copy(sort = SearchSort.Relevance))
    }
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        chips.forEach { (label, without) ->
            val shape = RoundedCornerShape(11.dp)
            Row(
                modifier = Modifier
                    .clip(shape)
                    .background(RangoColors.Primary.copy(alpha = 0.1f), shape)
                    .border(1.dp, RangoColors.Primary, shape)
                    .clickable { onApply(without) }
                    .padding(horizontal = 11.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(label, color = RangoColors.Primary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                Icon(RangoIcons.Close, contentDescription = null, tint = RangoColors.Primary, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IdleContent(
    recentSearches: List<String>,
    suggestions: List<SearchSuggestion>,
    onPick: (String) -> Unit,
    onClearRecent: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionTitle("Buscas recentes")
            if (recentSearches.isNotEmpty()) {
                Text(
                    "Limpar",
                    color = RangoColors.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onClearRecent),
                )
            }
        }
        if (recentSearches.isEmpty()) {
            Text("Nenhuma busca recente.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentSearches.forEach { term ->
                    val shape = RoundedCornerShape(12.dp)
                    Row(
                        modifier = Modifier
                            .clip(shape)
                            .background(MaterialTheme.colorScheme.surface, shape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                            .clickable { onPick(term) }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(RangoIcons.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        Text(term, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (suggestions.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
            SectionTitle("Sugestões para você")
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                suggestions.forEach { SuggestionRow(it, onClick = { onPick(it.label) }) }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun SuggestionRow(suggestion: SearchSuggestion, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(RangoColors.Primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(RangoIcons.TrendingUp, contentDescription = null, tint = RangoColors.Primary, modifier = Modifier.size(19.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(suggestion.label, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            if (suggestion.hint.isNotEmpty()) {
                Text(suggestion.hint, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        Icon(RangoIcons.ArrowUpRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun Results(
    results: List<Product>,
    sort: SearchSort,
    favoriteIds: Set<String>,
    onOpenProduct: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(resultsLabel(results.size), color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(sort.shortLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        results.forEach { product ->
            ResultCard(
                product = product,
                isFavorite = product.id in favoriteIds,
                onOpen = { onOpenProduct(product.id) },
                onToggleFavorite = { onToggleFavorite(product.id) },
                onAdd = { onAddToCart(product.id) },
            )
        }
    }
}

// Card horizontal dos resultados: foto, nome, restaurante e categoria, nota, tempo, preço e ações.
@Composable
private fun ResultCard(
    product: Product,
    isFavorite: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAdd: () -> Unit,
) {
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
            Text(
                product.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(onClick = onOpen),
            )
            Text(
                "${product.restaurant} · ${product.category}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(RangoIcons.Star, contentDescription = null, tint = Star, modifier = Modifier.size(14.dp))
                    Text(formatRating(product.rating), color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    "${product.minMinutes}-${product.maxMinutes} min",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(formatMoney(product.price), color = RangoColors.Primary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    val small = RoundedCornerShape(11.dp)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(small)
                            .background(MaterialTheme.colorScheme.surface, small)
                            .border(1.dp, MaterialTheme.colorScheme.outline, small)
                            .clickable(onClick = onToggleFavorite),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (isFavorite) RangoIcons.HeartFilled else RangoIcons.Heart,
                            contentDescription = null,
                            tint = if (isFavorite) RangoColors.Error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(small)
                            .background(RangoColors.Primary, small)
                            .clickable(onClick = onAdd),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(RangoIcons.Plus, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
                    }
                }
            }
        }
    }
}
