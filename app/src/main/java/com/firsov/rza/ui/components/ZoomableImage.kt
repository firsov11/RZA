package com.firsov.rza.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun ZoomableImage(bitmap: ImageBitmap) {
    var rawScale by remember { mutableStateOf(1f) }
    var rawOffsetX by remember { mutableStateOf(0f) }
    var rawOffsetY by remember { mutableStateOf(0f) }
    var lastTapTime by remember { mutableStateOf(0L) }

    // анимируемые состояния с "bouncy" spring
    val scale by animateFloatAsState(
        targetValue = rawScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val offsetX by animateFloatAsState(
        targetValue = rawOffsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val offsetY by animateFloatAsState(
        targetValue = rawOffsetY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // масштабируем всегда
                    rawScale = (rawScale * zoom).coerceIn(1f, 5f)

                    // перемещаем только если масштаб больше 1
                    if (rawScale > 1f) {
                        rawOffsetX += pan.x
                        rawOffsetY += pan.y
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastTapTime < 300) {
                            // двойной тап → сброс с "bouncy" анимацией
                            rawScale = 1f
                            rawOffsetX = 0f
                            rawOffsetY = 0f
                        }
                        lastTapTime = currentTime
                    }
                )
            }
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
        )
    }
}
