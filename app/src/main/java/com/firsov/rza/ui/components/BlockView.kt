package com.firsov.rza.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.*

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
            val bitmap = remember(block.bytes) {
                BitmapFactory.decodeByteArray(
                    block.bytes,
                    0,
                    block.bytes.size
                )?.asImageBitmap()
            }

            bitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )
            }
        }

        is DocxTable -> {
            DocxTableView(block.rows)
        }
    }
}
