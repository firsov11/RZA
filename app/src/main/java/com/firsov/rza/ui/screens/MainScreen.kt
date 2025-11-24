package com.firsov.rza.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.firsov.rza.ui.components.DocxViewer
import com.firsov.rza.ui.components.DrawerContent
import com.firsov.rza.viewmodel.DocxViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: DocxViewModel = hiltViewModel()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadFiles(context)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(viewModel.files.value) { filename ->
                viewModel.openFile(context, filename)
                scope.launch { drawerState.close() }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Docx Reader") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    }
                )
            }
        ) { padding ->
            viewModel.currentDoc.value?.let {
                DocxViewer(it.chapters)
            } ?: run {
                Text("Выберите файл", modifier = Modifier.padding(padding))
            }
        }
    }
}
