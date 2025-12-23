package com.firsov.rza.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.SimpleTable
import com.firsov.rza.data.models.TableCellContent

@Composable
fun DocxTableView(rows: SimpleTable) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
    ) {

        rows.forEachIndexed { rowIndex, row ->

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                row.forEachIndexed { cellIndex, cell ->

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(6.dp)
                    ) {
                        when (cell) {

                            is TableCellContent.Text -> {
                                Text(text = cell.value)
                            }

                            is TableCellContent.Image -> {
                                val bitmap = remember(cell.bytes) {
                                    BitmapFactory
                                        .decodeByteArray(
                                            cell.bytes,
                                            0,
                                            cell.bytes.size
                                        )
                                        ?.asImageBitmap()
                                }

                                bitmap?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // ❌ ФОРМУЛ НЕТ
                        }
                    }

                    // вертикальная линия
                    if (cellIndex < row.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(Color.Gray)
                        )
                    }
                }
            }

            // горизонтальная линия
            if (rowIndex < rows.lastIndex) {
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color.Gray)
                )
            }
        }
    }
}
