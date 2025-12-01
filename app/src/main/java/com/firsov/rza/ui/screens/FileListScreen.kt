package com.firsov.rza.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.firsov.rza.viewmodel.DocxViewModel

@Composable
fun FileListScreen(
    vm: DocxViewModel,
    onOpen: (String) -> Unit
) {
    val files by vm.files.collectAsState()
    val context = LocalContext.current

    // ВОТ ЭТО ОБЯЗАТЕЛЬНО
    LaunchedEffect(Unit) {
        vm.loadFiles(context)
    }

    Column(Modifier.padding(16.dp)) {
        Text("Файлы .docx", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        files.forEach { f ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onOpen(f) }
            ) {
                Text(
                    f,
                    Modifier.padding(16.dp)
                )
            }
        }
    }
}

