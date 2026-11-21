package org.example.project.ui.profile

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.data.UserProfile
import org.example.project.data.avatarInitials
import org.example.project.ui.checkout.Divider
import org.example.project.ui.checkout.PrimaryButton
import org.example.project.ui.components.AuthField
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

private val EmailPattern = Regex("^[^@\\s]+@[^@\\s]+\\.[a-z]{2,}$", RegexOption.IGNORE_CASE)

@Composable
fun EditProfileScreen(
    profile: UserProfile,
    avatar: ImageBitmap?,
    onBack: () -> Unit,
    // Salva no servidor e devolve os erros por campo ("name", "email", "phone"); vazio = salvo.
    onSave: suspend (name: String, email: String, phone: String) -> Map<String, String>,
    onTakePhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }
    var phone by remember { mutableStateOf(profile.phone) }
    var errors by remember { mutableStateOf(mapOf<String, String>()) }
    var saving by remember { mutableStateOf(false) }
    var photoSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Mesmas regras do cadastro; o servidor confere de novo (ex.: e-mail já usado).
    fun save() {
        if (saving) return
        errors = buildMap {
            if (name.trim().length < 3) put("name", "Informe seu nome completo.")
            if (!EmailPattern.matches(email.trim())) put("email", "E-mail inválido.")
            if (phone.count { it.isDigit() } !in 10..11) put("phone", "Telefone incompleto.")
        }
        if (errors.isNotEmpty()) return
        scope.launch {
            saving = true
            val result = onSave(name, email, phone)
            saving = false
            if (result.isEmpty()) onBack() else errors = result
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            EditProfileHeader(onBack = onBack, onSave = ::save)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(11.dp),
                ) {
                    EditableAvatar(name = profile.name, avatar = avatar, onClick = { photoSheet = true })
                    Text(
                        "Alterar foto",
                        color = RangoColors.Primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { photoSheet = true }.padding(4.dp),
                    )
                }
                AuthField(
                    label = "Nome completo",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Seu nome",
                    error = errors["name"].orEmpty(),
                    keyboardType = KeyboardType.Text,
                    leadingIcon = RangoIcons.User,
                )
                AuthField(
                    label = "E-mail",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "voce@email.com",
                    error = errors["email"].orEmpty(),
                    keyboardType = KeyboardType.Email,
                    leadingIcon = RangoIcons.Mail,
                )
                AuthField(
                    label = "Telefone",
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "(11) 90000-0000",
                    error = errors["phone"].orEmpty(),
                    keyboardType = KeyboardType.Phone,
                    leadingIcon = RangoIcons.Phone,
                )
            }
            Divider()
            Box(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 20.dp)) {
                PrimaryButton(if (saving) "Salvando..." else "Salvar alterações", onClick = ::save, enabled = !saving)
            }
        }
    }

    if (photoSheet) {
        PhotoSheet(
            hasPhoto = avatar != null,
            onDismiss = { photoSheet = false },
            onTakePhoto = {
                photoSheet = false
                onTakePhoto()
            },
            onRemovePhoto = {
                photoSheet = false
                onRemovePhoto()
            },
        )
    }
}

// Cabeçalho com voltar, título e "Salvar" em laranja, como no protótipo.
@Composable
private fun EditProfileHeader(onBack: () -> Unit, onSave: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val shape = RoundedCornerShape(13.dp)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(RangoIcons.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(21.dp))
            }
            Text(
                "Editar perfil",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                "Salvar",
                color = RangoColors.Primary,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onSave).padding(4.dp),
            )
        }
        Divider()
    }
}

// Avatar grande com o selo da câmera no canto.
@Composable
private fun EditableAvatar(name: String, avatar: ImageBitmap?, onClick: () -> Unit) {
    val shape = RoundedCornerShape(34.dp)
    Box(modifier = Modifier.size(106.dp).clickable(onClick = onClick)) {
        AvatarImage(name = name, avatar = avatar, shape = shape, fontSize = 30, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 2.dp, y = 2.dp)
                .size(34.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .padding(3.dp)
                .background(RangoColors.Primary, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(RangoIcons.Camera, contentDescription = "Alterar foto", tint = Color.White, modifier = Modifier.size(17.dp))
        }
    }
}

// Foto de perfil ou, sem foto, as iniciais em laranja (usado também no Perfil).
@Composable
internal fun AvatarImage(
    name: String,
    avatar: ImageBitmap?,
    shape: RoundedCornerShape,
    fontSize: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (avatar != null) {
            Image(avatar, contentDescription = "Foto de perfil", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Text(avatarInitials(name), color = RangoColors.Primary, fontSize = fontSize.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

// Folha "Foto do perfil": tirar foto pela câmera ou remover a atual.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoSheet(hasPhoto: Boolean, onDismiss: () -> Unit, onTakePhoto: () -> Unit, onRemovePhoto: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
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
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 18.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Foto do perfil",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            PhotoAction(RangoIcons.Camera, "Tirar foto", danger = false, onClick = onTakePhoto)
            if (hasPhoto) PhotoAction(RangoIcons.Trash, "Remover foto", danger = true, onClick = onRemovePhoto)
            val shape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(shape)
                    .border(1.5.dp, MaterialTheme.colorScheme.outline, shape)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PhotoAction(icon: ImageVector, label: String, danger: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(17.dp)
    val accent = if (danger) RangoColors.Error else RangoColors.Primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .clickable(onClick = onClick)
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
        Text(
            label,
            color = if (danger) RangoColors.Error else MaterialTheme.colorScheme.onSurface,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
