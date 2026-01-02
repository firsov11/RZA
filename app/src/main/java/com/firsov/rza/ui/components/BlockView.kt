package com.firsov.rza.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.DocxBlock
import com.firsov.rza.data.models.DocxImage
import com.firsov.rza.data.models.DocxTable
import com.firsov.rza.data.models.DocxText

@Composable
fun BlockView(block: DocxBlock) {
    when (block) {
        is DocxText -> {
            Text(
                text = block.text,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        is DocxImage -> {
            val imageBitmap = remember(block.bytes) {
                block.image ?: BitmapFactory.decodeByteArray(
                    block.bytes,
                    0,
                    block.bytes.size
                )?.asImageBitmap()
            }

            imageBitmap?.let {
                ZoomableImage(bitmap = it)
            }
        }

        is DocxTable -> {
            DocxTableView(block.table)
        }
    }
}
