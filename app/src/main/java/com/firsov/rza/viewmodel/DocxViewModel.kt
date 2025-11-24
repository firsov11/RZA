package com.firsov.rza.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firsov.rza.data.models.DocxDocument
import com.firsov.rza.data.repository.DocxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocxViewModel @Inject constructor(
    private val repository: DocxRepository
) : ViewModel() {

    var files = mutableStateOf(listOf<String>())
        private set

    var currentDoc = mutableStateOf<DocxDocument?>(null)
        private set

    fun loadFiles(context: Context) {
        files.value = repository.listDocxFiles(context)
    }

    fun openFile(context: Context, filename: String) {
        viewModelScope.launch {
            currentDoc.value = repository.getDocxDocument(context, filename)
        }
    }
}

