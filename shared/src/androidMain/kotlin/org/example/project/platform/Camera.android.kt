package org.example.project.platform

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
actual fun rememberCameraLauncher(onPhoto: (ByteArray) -> Unit, onDenied: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnPhoto by rememberUpdatedState(onPhoto)
    val currentOnDenied by rememberUpdatedState(onDenied)
    // A câmera grava a foto em tamanho original neste arquivo temporário (compartilhado via FileProvider).
    val file = remember { File(context.cacheDir, "camera/avatar.jpg").also { it.parentFile?.mkdirs() } }
    val uri = remember { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file) }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        if (!saved) return@rememberLauncherForActivityResult
        scope.launch {
            // Decodificar e recortar uma foto grande leva tempo: fora da thread principal.
            val jpeg = withContext(Dispatchers.Default) { squareJpeg(file).also { file.delete() } }
            jpeg?.let(currentOnPhoto)
        }
    }
    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) takePicture.launch(uri) else currentOnDenied()
    }

    return {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (granted) takePicture.launch(uri) else requestPermission.launch(Manifest.permission.CAMERA)
    }
}

// Recorta o centro em quadrado, reduz para AvatarSizePx, corrige a rotação da foto e gera o JPEG.
private fun squareJpeg(file: File): ByteArray? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.path, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    // Já lê reduzida (potência de 2) para não carregar a foto inteira na memória.
    var sample = 1
    while (minOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= AvatarSizePx) sample *= 2
    val source = BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = sample }) ?: return null

    val side = minOf(source.width, source.height)
    val matrix = Matrix().apply {
        val scale = AvatarSizePx.toFloat() / side
        postScale(scale, scale)
        postRotate(exifRotation(file).toFloat())
    }
    val square = Bitmap.createBitmap(source, (source.width - side) / 2, (source.height - side) / 2, side, side, matrix, true)
    return ByteArrayOutputStream().use { out ->
        square.compress(Bitmap.CompressFormat.JPEG, 85, out)
        out.toByteArray()
    }
}

// A câmera grava a foto "deitada" e informa a rotação no EXIF.
private fun exifRotation(file: File): Int =
    when (ExifInterface(file.path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
