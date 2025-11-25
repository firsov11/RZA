package com.firsov.rza.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firsov.rza.ui.components.DocxDocumentView
import com.firsov.rza.viewmodel.DocxViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: DocxViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val files by viewModel.files.collectAsState()
    val document by viewModel.document.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadFiles(context)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Документы", modifier = Modifier.padding(16.dp))

                if (files.isEmpty()) {
                    Text(
                        text = "Нет документов в assets",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                files.forEach { file ->
                    Text(
                        text = file,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.openFile(context, file)
                                scope.launch { drawerState.close() }
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("RZA Docx Viewer") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (val doc = document) {
                    null -> Text("Выберите документ", modifier = Modifier.padding(16.dp))
                    else -> DocxDocumentView(doc)
                }
            }
        }
    }
}
