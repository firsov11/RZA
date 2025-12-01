package com.firsov.rza.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firsov.rza.data.models.DocxDocument
import com.firsov.rza.data.repository.DocxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocxViewModel @Inject constructor(
    private val repository: DocxRepository
) : ViewModel() {

    // список файлов
    private val _files = MutableStateFlow<List<String>>(emptyList())
    val files = _files.asStateFlow()

    // текущий открытый документ
    private val _document = MutableStateFlow<DocxDocument?>(null)
    val document = _document.asStateFlow()

    // загружаем файлы из assets/docs
    fun loadFiles(context: Context) {
        _files.value = repository.listDocxFiles(context)
    }

    // открываем файл
    fun openFile(context: Context, filename: String) {
        viewModelScope.launch {
            try {
                val doc = repository.getDocxDocument(context, filename)
                _document.value = doc
                repository.loadImagesLazy(doc.chapters)
            } catch (e: Exception) {
                _document.value = null
            }
        }
    }

    // закрываем документ
    fun closeDocument() {
        _document.value = null
    }
}
