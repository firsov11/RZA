package com.firsov.rza.ui.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.*

@Composable
fun rememberImage(bytes: ByteArray) = remember(bytes) {
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
