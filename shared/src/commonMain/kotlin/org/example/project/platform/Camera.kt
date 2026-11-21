package org.example.project.platform

import androidx.compose.runtime.Composable

// Lado da foto de perfil enviada ao servidor (recorte quadrado, como no protótipo: 512x512 px).
const val AvatarSizePx = 512

// Abre a câmera do aparelho e devolve a foto já recortada em quadrado, reduzida e em JPEG.
// Pede a permissão de câmera antes, se preciso; onDenied é chamado quando ela é negada.
// Cancelar a câmera não chama nenhum dos dois.
@Composable
expect fun rememberCameraLauncher(onPhoto: (ByteArray) -> Unit, onDenied: () -> Unit): () -> Unit
