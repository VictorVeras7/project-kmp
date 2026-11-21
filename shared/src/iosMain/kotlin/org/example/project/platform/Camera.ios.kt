package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIApplication
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIGraphicsImageRendererFormat
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy

// Escrito sem biblioteca, para medir o custo real do ponto de plataforma. Só compila e roda no Mac.
@Composable
actual fun rememberCameraLauncher(onPhoto: (ByteArray) -> Unit, onDenied: () -> Unit): () -> Unit {
    val currentOnPhoto by rememberUpdatedState(onPhoto)
    val currentOnDenied by rememberUpdatedState(onDenied)
    // O UIKit guarda o delegate como referência fraca: o remember o mantém vivo enquanto a tela existe.
    val delegate = remember { CameraDelegate() }
    delegate.onPhoto = { currentOnPhoto(it) }

    return remember {
        {
            val sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            if (!UIImagePickerController.isSourceTypeAvailable(sourceType)) {
                // Simulador não tem câmera.
                currentOnDenied()
            } else {
                withCameraPermission(onGranted = { presentPicker(delegate) }, onDenied = { currentOnDenied() })
            }
        }
    }
}

// A primeira vez mostra o pedido do sistema (texto do NSCameraUsageDescription no Info.plist).
private fun withCameraPermission(onGranted: () -> Unit, onDenied: () -> Unit) {
    when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
        AVAuthorizationStatusAuthorized -> onGranted()
        AVAuthorizationStatusNotDetermined -> AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
            dispatch_async(dispatch_get_main_queue()) { if (granted) onGranted() else onDenied() }
        }
        else -> onDenied()
    }
}

private fun presentPicker(delegate: CameraDelegate) {
    val picker = UIImagePickerController()
    picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
    picker.delegate = delegate
    UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(picker, animated = true, completion = null)
}

private class CameraDelegate : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    var onPhoto: (ByteArray) -> Unit = {}

    override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        picker.dismissViewControllerAnimated(true, completion = null)
        image?.let(::squareJpeg)?.let(onPhoto)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

// Recorta o centro em quadrado de AvatarSizePx e gera o JPEG. drawInRect já aplica a rotação da foto.
@OptIn(ExperimentalForeignApi::class)
private fun squareJpeg(image: UIImage): ByteArray? {
    val (width, height) = image.size.useContents { width to height }
    val target = AvatarSizePx.toDouble()
    val scale = target / minOf(width, height)
    val format = UIGraphicsImageRendererFormat.defaultFormat()
    format.scale = 1.0
    val renderer = UIGraphicsImageRenderer(size = CGSizeMake(target, target), format = format)
    val square = renderer.imageWithActions { _ ->
        val w = width * scale
        val h = height * scale
        image.drawInRect(CGRectMake((target - w) / 2, (target - h) / 2, w, h))
    }
    val data = UIImageJPEGRepresentation(square, 0.85) ?: return null
    val bytes = ByteArray(data.length.toInt())
    if (bytes.isNotEmpty()) bytes.usePinned { memcpy(it.addressOf(0), data.bytes, data.length) }
    return bytes
}
