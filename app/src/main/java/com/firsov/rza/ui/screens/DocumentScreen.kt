package com.firsov.rza.ui.screens

import BlockView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.firsov.rza.viewmodel.DocxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(
    vm: DocxViewModel,
    filename: String,
    onBack: () -> Unit
) {
    val doc by vm.document.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(filename) {
        vm.openFile(context, filename)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(filename) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        if (doc == null) {
            Box(Modifier.padding(padding).padding(16.dp)) {
                Text("Загрузка...")
            }
        } else {
            Column(
                Modifier
                    .padding(padding)
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                doc!!.chapters.forEach { chapter ->
                    Text(
                        chapter.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    chapter.blocks.forEach { block ->
                        BlockView(block)
                    }
                }
            }
        }
    }
}
