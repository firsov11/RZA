package com.firsov.rza.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.Chapter

@Composable
fun DocxViewer(chapters: List<Chapter>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        chapters.forEach { chapter ->
            item {
                Text(text = chapter.title, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }
            chapter.paragraphs.forEach { paragraph ->
                item {
                    Text(text = paragraph)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            chapter.tables.forEach { table ->
                item {
                    Column {
                        table.forEach { row ->
                            Row {
                                row.forEach { cell ->
                                    Text("$cell | ")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            chapter.images.forEach { image ->
                item {
                    Image(bitmap = image.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxWidth().padding(8.dp))
                }
            }
        }
    }
}

