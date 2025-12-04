package com.firsov.rza.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firsov.rza.data.models.DocxDocument
import com.firsov.rza.data.repository.DocxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class DocxViewModel @Inject constructor(
    private val repository: DocxRepository
) : ViewModel() {

    private val _files = MutableStateFlow<List<String>>(emptyList())
    val files = _files.asStateFlow()

    private val _document = MutableStateFlow<DocxDocument?>(null)
    val document = _document.asStateFlow()

    // загружаем список файлов
    fun loadFiles(context: Context) {
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    repository.listDocxFiles(context)
                }
                _files.value = list
                Log.d("DocxViewModel", "Files loaded: ${list.size}")
            } catch (e: Exception) {
                Log.e("DocxViewModel", "Failed to load files", e)
            }
        }
    }

    // открываем файл
    fun openFile(context: Context, filename: String) {
        viewModelScope.launch {
            try {
                Log.d("DocxViewModel", "Opening file: $filename")

                val doc = withContext(Dispatchers.IO) {
                    repository.getDocxDocument(context, filename)
                }

                _document.value = doc

                // загрузка изображений тоже в фоне
                withContext(Dispatchers.IO) {
                    repository.loadImagesLazy(doc.chapters)
                }
                Log.d("DocxViewModel", "Images loaded")
            } catch (e: Exception) {
                _document.value = null
                Log.e("DocxViewModel", "Failed to open file: $filename", e)
            }
        }
    }

    // закрываем документ
    fun closeDocument() {
        _document.value = null
        Log.d("DocxViewModel", "Document closed")
    }
}
