package com.firsov.rza.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.Chapter
import BlockView

@Composable
fun DocxDocumentView(chapters: List<Chapter>) {
    LazyColumn(modifier = Modifier.padding(12.dp)) {

        chapters.forEach { chapter ->

            item {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(chapter.blocks) { block ->
                BlockView(block)
            }
        }
    }
}
