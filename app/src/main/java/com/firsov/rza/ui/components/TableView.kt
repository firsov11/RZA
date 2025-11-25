package com.firsov.rza.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.DocxTable

@Composable
fun TableView(table: DocxTable) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        table.rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { cell ->
                    Text(
                        text = cell,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
