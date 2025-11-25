package com.firsov.rza.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firsov.rza.data.models.DocxDocument
import com.firsov.rza.data.repository.DocxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocxViewModel @Inject constructor(
    private val repository: DocxRepository
) : ViewModel() {

    private val _files = MutableStateFlow<List<String>>(emptyList())
    val files = _files.asStateFlow()

    private val _document = MutableStateFlow<DocxDocument?>(null)
    val document: StateFlow<DocxDocument?> = _document

    fun loadFiles(context: Context) {
        _files.value = repository.listDocxFiles(context)
    }

    fun openFile(context: Context, filename: String) {
        viewModelScope.launch {
            val doc = repository.getDocxDocument(context, filename)
            _document.value = doc
            // ленивое подгружение картинок
            repository.loadImagesLazy(doc.chapters)
        }
    }
}
