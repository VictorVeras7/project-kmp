package org.example.project.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.PaymentMethod
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons

// Estrutura comum das telas do checkout: cabeçalho, conteúdo rolável e barra inferior.
@Composable
internal fun CheckoutScaffold(
    title: String,
    onBack: () -> Unit,
    bottomBar: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    contentSpacing: Dp = 14.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Fundo branco atrás da barra de status, igual ao cabeçalho; conteúdo em cinza claro.
    // A margem inferior do sistema fica na barra de baixo ou, sem ela, no próprio conteúdo cinza.
    val bottomInset = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
        ) {
            CheckoutHeader(title, subtitle, onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .then(if (bottomBar == null) Modifier.windowInsetsPadding(bottomInset) else Modifier)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(contentSpacing),
                content = content,
            )
            if (bottomBar != null) {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface).windowInsetsPadding(bottomInset)) {
                    Divider()
                    Box(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 20.dp)) {
                        bottomBar()
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutHeader(title: String, subtitle: String?, onBack: () -> Unit) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val shape = RoundedCornerShape(13.dp)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface, shape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    RangoIcons.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(21.dp),
                )
            }
            Column {
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
        Divider()
    }
}

@Composable
internal fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
}

@Composable
internal fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(17.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(10.dp, shape, ambientColor = RangoColors.Primary, spotColor = RangoColors.Primary),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            leading?.invoke()
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Card branco com borda, usado nos blocos do checkout.
@Composable
internal fun CheckoutCard(
    modifier: Modifier = Modifier,
    spacing: Dp = 11.dp,
    highlighted: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(
                if (highlighted) 1.5.dp else 1.dp,
                if (highlighted) RangoColors.Primary else MaterialTheme.colorScheme.outline,
                shape,
            )
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}

// Linha selecionável (endereço ou pagamento) com fundo laranja suave quando marcada.
@Composable
internal fun SelectableRow(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(17.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) RangoColors.Primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface, shape)
            .border(1.5.dp, if (selected) RangoColors.Primary else MaterialTheme.colorScheme.outline, shape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        content()
    }
}

@Composable
internal fun RadioDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(21.dp)
            .border(2.dp, if (selected) RangoColors.Primary else MaterialTheme.colorScheme.outline, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) Box(Modifier.size(11.dp).background(RangoColors.Primary, CircleShape))
    }
}

// Ícone laranja sobre fundo laranja suave (pagamento).
@Composable
internal fun SoftIcon(icon: ImageVector, size: Dp = 40.dp, iconSize: Dp = 21.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(RangoColors.Primary.copy(alpha = 0.1f), RoundedCornerShape(size / 3)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = RangoColors.Primary, modifier = Modifier.size(iconSize))
    }
}

@Composable
internal fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    error: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .border(1.dp, if (error) RangoColors.Error else MaterialTheme.colorScheme.outline, shape)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            placeholder,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    inner()
                }
            },
        )
    }
}

// Mapa ilustrativo (endereço e acompanhamento): fundo verde com listras e etiqueta no canto.
@Composable
internal fun MapPlaceholder(
    label: String,
    height: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(if (height > 140.dp) 22.dp else 20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(MapColor)
            .drawBehind {
                // Listras diagonais de 10dp, como o repeating-linear-gradient do protótipo.
                val step = 20.dp.toPx()
                val stripe = 10.dp.toPx()
                var x = -size.height
                while (x < size.width) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.05f),
                        start = Offset(x, size.height),
                        end = Offset(x + size.height, 0f),
                        strokeWidth = stripe,
                    )
                    x += step * 1.414f
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
        Text(
            label,
            color = Color.Black.copy(alpha = 0.5f),
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 10.dp)
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 3.dp),
        )
    }
}

private val MapColor = Color(0xFFCFE3D8)

internal val PaymentMethod.icon: ImageVector
    get() = when (this) {
        PaymentMethod.Pix -> RangoIcons.QrCode
        PaymentMethod.Credit -> RangoIcons.CreditCard
        PaymentMethod.Debit -> RangoIcons.DebitCard
        PaymentMethod.Cash -> RangoIcons.Cash
    }
