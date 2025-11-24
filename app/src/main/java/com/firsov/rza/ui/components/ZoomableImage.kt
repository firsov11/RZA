package com.firsov.rza.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun ZoomableImage(bitmap: androidx.compose.ui.graphics.ImageBitmap) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(bitmap) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset += pan
                    if (scale < 1f) scale = 1f
                    if (scale > 5f) scale = 5f
                }
            }
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
        )
    }
}
