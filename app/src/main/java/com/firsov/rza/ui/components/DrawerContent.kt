package com.firsov.rza.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContent(files: List<String>, onFileClick: (String) -> Unit) {
    Column {
        files.forEach { file ->
            Text(
                text = file,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFileClick(file) }
                    .padding(16.dp)
            )
        }
    }
}
