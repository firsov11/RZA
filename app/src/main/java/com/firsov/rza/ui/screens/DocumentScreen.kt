package com.firsov.rza.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.firsov.rza.ui.components.BlockView
import com.firsov.rza.viewmodel.DocxUiState
import com.firsov.rza.viewmodel.DocxViewModel
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(
    vm: DocxViewModel,
    filename: String,
    onBack: () -> Unit
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(filename) {
        vm.openFile(context, filename)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(filename) },
                navigationIcon = {
                    IconButton(onClick = {
                        vm.closeDocument()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        when (state) {
            DocxUiState.Idle,
            DocxUiState.Loading -> {
                Box(
                    Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text("Загрузка...")
                }
            }

            is DocxUiState.Error -> {
                Box(
                    Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text(
                        (state as DocxUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is DocxUiState.Success -> {
                val doc = (state as DocxUiState.Success).document

                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = 12.dp)
                ) {
                    doc.chapters.forEach { chapter ->

                        // Заголовок главы
                        item(key = "title_${chapter.title}") {
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Блоки главы
                        items(
                            items = chapter.blocks,
                            key = { it.hashCode() } // важно для стабильности
                        ) { block ->
                            BlockView(block)
                        }
                    }
                }
            }
        }
    }
}


