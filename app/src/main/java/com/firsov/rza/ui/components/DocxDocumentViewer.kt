package com.firsov.rza.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.*

@Composable
fun DocxDocumentView(doc: DocxDocument) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        doc.chapters.forEach { chapter ->

            item {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            chapter.blocks.forEach { block ->

                when (block) {

                    is DocxParagraph -> item {
                        ParagraphView(block)
                    }

                    is DocxHeading -> item {
                        Text(
                            text = block.text,
                            style = when (block.level) {
                                1 -> MaterialTheme.typography.headlineSmall
                                2 -> MaterialTheme.typography.titleLarge
                                else -> MaterialTheme.typography.titleMedium
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    is DocxTable -> item {
                        TableView(block)
                    }

                    is DocxImageLazy -> item {
                        ImageLazyView(block)
                    }
                }
            }
        }
    }
}
