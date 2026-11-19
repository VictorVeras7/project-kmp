package org.example.project.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.checkout.Divider
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons

// Grupo com título em caixa alta e as linhas dentro de um card (tela Configurações).
@Composable
internal fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title.uppercase(),
            modifier = Modifier.padding(start = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.9.sp,
        )
        SettingsCard(content = content)
    }
}

// Card branco com borda; as linhas ficam separadas por uma linha fina.
@Composable
internal fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape),
        content = content,
    )
}

// Linha com ícone em fundo suave, rótulo, dica opcional e um controle à direita (seta, chave ou rádio).
// "detailed" segue as telas Notificações e Tema, que usam a dica um pouco maior.
@Composable
internal fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    first: Boolean,
    hint: String = "",
    danger: Boolean = false,
    detailed: Boolean = false,
    role: Role = Role.Button,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val accent = if (danger) RangoColors.Error else RangoColors.Primary
    if (!first) Divider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = role, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(accent.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(19.dp))
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(if (detailed) 2.dp else 1.dp),
        ) {
            Text(
                label,
                color = if (danger) RangoColors.Error else MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            if (hint.isNotEmpty()) {
                Text(
                    hint,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = if (detailed) 11.5.sp else 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        trailing()
    }
}

// Valor atual em laranja antes da seta (ex.: "Claro" na linha Tema).
@Composable
internal fun RowValue(text: String) {
    Text(text, color = RangoColors.Primary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
}

@Composable
internal fun Chevron() {
    Icon(RangoIcons.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
}
