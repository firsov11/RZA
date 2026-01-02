package com.firsov.rza.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.DocxTableModel
import com.firsov.rza.data.models.TableCellContent

@Composable
fun DocxTableView(table: DocxTableModel) {
    val borderColor = Color(0xFF6E6E6E)
    val outerBorder = 1.5.dp
    val innerBorder = 0.8.dp

    Column(
        modifier = Modifier
            .fillMaxWidth() // Таблица растягивается на весь экран
            .border(outerBorder, borderColor)
            .padding(vertical = 8.dp)
    ) {
        table.rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min) // Высота строки = высота самой высокой ячейки
            ) {
                row.cells.forEach { cell ->
                    if (cell == null) return@forEach // Продолжение объединённой ячейки → пропускаем

                    Box(
                        modifier = Modifier
                            .weight(cell.colSpan.toFloat(), fill = true)
                            .fillMaxHeight()
                            .border(innerBorder, borderColor)
                            .padding(8.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        when (val content = cell.content) {
                            is TableCellContent.Text -> {
                                Text(
                                    text = content.value,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            is TableCellContent.Image -> {
                                val bitmap = remember(content.bytes) {
                                    BitmapFactory.decodeByteArray(
                                        content.bytes,
                                        0,
                                        content.bytes.size
                                    )?.asImageBitmap()
                                }

                                bitmap?.let { ZoomableImage(bitmap = it) }
                            }
                        }
                    }
                }
            }
        }
    }
}

