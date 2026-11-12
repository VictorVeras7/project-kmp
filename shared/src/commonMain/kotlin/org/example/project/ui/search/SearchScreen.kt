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
