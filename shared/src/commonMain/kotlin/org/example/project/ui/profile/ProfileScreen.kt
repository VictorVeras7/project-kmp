package org.example.project.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.ThemeMode
import org.example.project.data.UserProfile
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

@Composable
fun ProfileScreen(
    profile: UserProfile,
    avatar: ImageBitmap?,
    addressCount: Int,
    paymentCount: Int,
    unreadNotifications: Int,
    theme: ThemeMode,
    onOpenNotifications: () -> Unit,
    onEditProfile: () -> Unit,
    onAddresses: () -> Unit,
    onPayments: () -> Unit,
    onSettings: () -> Unit,
    onTheme: () -> Unit,
    onHelp: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Mesma ordem, ícones e dicas das linhas do protótipo; "Sair da conta" fica em vermelho e sem seta.
    val rows = listOf(
        ProfileRow(RangoIcons.User, "Editar perfil", "nome, e-mail e telefone", onClick = onEditProfile),
        ProfileRow(RangoIcons.Pin, "Endereços", addressesHint(addressCount), onClick = onAddresses),
        ProfileRow(RangoIcons.Wallet, "Formas de pagamento", paymentsHint(paymentCount), onClick = onPayments),
        ProfileRow(RangoIcons.Settings, "Configurações", onClick = onSettings),
        ProfileRow(RangoIcons.Bell, "Notificações", unreadHint(unreadNotifications), onClick = onOpenNotifications),
        ProfileRow(RangoIcons.DarkMode, "Tema", theme.label, onClick = onTheme),
        ProfileRow(RangoIcons.Help, "Ajuda", onClick = onHelp),
        ProfileRow(RangoIcons.Logout, "Sair da conta", danger = true, onClick = onLogout),
    )
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .verticalScroll(rememberScrollState()),
        ) {
            ProfileHeader(
                profile = profile,
                avatar = avatar,
                hasUnread = unreadNotifications > 0,
                onOpenNotifications = onOpenNotifications,
                onEditProfile = onEditProfile,
            )
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rows.forEach { ProfileRowItem(it) }
            }
        }
    }
}

private class ProfileRow(
    val icon: ImageVector,
    val label: String,
    val hint: String = "",
    val danger: Boolean = false,
    val onClick: () -> Unit,
)

private fun addressesHint(count: Int): String = when (count) {
    0 -> "nenhum salvo"
    1 -> "1 salvo"
    else -> "$count salvos"
}

private fun paymentsHint(count: Int): String = when (count) {
    0 -> ""
    1 -> "1 método disponível"
    else -> "$count métodos disponíveis"
}

private fun unreadHint(count: Int): String = when (count) {
    0 -> "tudo lido"
    1 -> "1 não lida"
    else -> "$count não lidas"
}

// Topo branco: título com o sino, avatar com nome, e-mail e telefone, e o botão "Editar perfil".
@Composable
private fun ProfileHeader(
    profile: UserProfile,
    avatar: ImageBitmap?,
    hasUnread: Boolean,
    onOpenNotifications: () -> Unit,
    onEditProfile: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Perfil",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.6).sp,
                )
                BellButton(hasUnread = hasUnread, onClick = onOpenNotifications)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AvatarImage(
                    name = profile.name,
                    avatar = avatar,
                    shape = RoundedCornerShape(22.dp),
                    fontSize = 21,
                    modifier = Modifier.size(66.dp),
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        profile.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.4).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // E-mail e telefone chegam com GET /me; até lá só o nome aparece.
                    listOf(profile.email, profile.phone).filter { it.isNotBlank() }.forEach { line ->
                        Text(
                            line,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            OutlinedButton(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.5.dp, RangoColors.Primary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RangoColors.Primary),
            ) {
                Text("Editar perfil", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
    }
}

// Sino do topo, com ponto vermelho quando há notificação não lida.
@Composable
private fun BellButton(hasUnread: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(13.dp)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(RangoIcons.Bell, contentDescription = "Notificações", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
        if (hasUnread) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 7.dp)
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .padding(1.5.dp)
                    .background(RangoColors.Error, CircleShape),
            )
        }
    }
}

@Composable
private fun ProfileRowItem(row: ProfileRow) {
    val shape = RoundedCornerShape(17.dp)
    val accent = if (row.danger) RangoColors.Error else RangoColors.Primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, if (row.danger) RangoColors.Error.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline, shape)
            .clickable(onClick = row.onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(accent.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(row.icon, contentDescription = null, tint = accent, modifier = Modifier.size(19.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                row.label,
                color = if (row.danger) RangoColors.Error else MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            if (row.hint.isNotEmpty()) {
                Text(row.hint, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        if (!row.danger) {
            Icon(RangoIcons.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

private val PreviewProfile = UserProfile(name = "Marina Alves", email = "marina.alves@email.com", phone = "(11) 98876-4410")

@Preview
@Composable
private fun ProfileScreenPreview() {
    RangoTheme(darkTheme = false) {
        ProfileScreen(
            profile = PreviewProfile,
            avatar = null,
            addressCount = 2,
            paymentCount = 4,
            unreadNotifications = 2,
            theme = ThemeMode.Light,
            onOpenNotifications = {},
            onEditProfile = {},
            onAddresses = {},
            onPayments = {},
            onSettings = {},
            onTheme = {},
            onHelp = {},
            onLogout = {},
        )
    }
}

@Preview
@Composable
private fun ProfileScreenDarkPreview() {
    RangoTheme(darkTheme = true) {
        ProfileScreen(
            profile = PreviewProfile,
            avatar = null,
            addressCount = 1,
            paymentCount = 4,
            unreadNotifications = 0,
            theme = ThemeMode.Dark,
            onOpenNotifications = {},
            onEditProfile = {},
            onAddresses = {},
            onPayments = {},
            onSettings = {},
            onTheme = {},
            onHelp = {},
            onLogout = {},
        )
    }
}
