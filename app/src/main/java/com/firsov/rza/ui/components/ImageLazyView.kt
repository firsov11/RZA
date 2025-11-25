package com.firsov.rza.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.firsov.rza.data.models.DocxImageLazy

@Composable
fun ImageLazyView(img: DocxImageLazy) {
    val context = LocalContext.current

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(img.bytes)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build()
    )

    ZoomableImage(
        painter = painter,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}
