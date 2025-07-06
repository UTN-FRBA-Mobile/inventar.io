package ar.edu.utn.frba.inventario.utils

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.drawScanOverlay(scanBoxPx: Float) {
    // Fullscreen dim
    drawRect(color = Color.Black.copy(alpha = 0.6f))

    // Transparent rounded cutout (centered)
    val centerX = size.width / 2
    val centerY = size.height / 2

    val rectLeft = centerX - scanBoxPx / 2
    val rectTop = centerY - scanBoxPx / 2

    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = rectLeft,
                top = rectTop,
                right = rectLeft + scanBoxPx,
                bottom = rectTop + scanBoxPx,
                cornerRadius = CornerRadius(32f, 32f),
            ),
        )
    }

    drawPath(path = path, color = Color.Transparent, blendMode = BlendMode.Clear)
}
