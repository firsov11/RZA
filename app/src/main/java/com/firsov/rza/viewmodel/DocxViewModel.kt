package com.firsov.rza.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firsov.rza.data.models.*
import com.firsov.rza.data.repository.DocxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface DocxUiState {
    object Idle : DocxUiState
    object Loading : DocxUiState
    data class Success(val document: DocxDocument) : DocxUiState
    data class Error(val message: String) : DocxUiState
}

@HiltViewModel
class DocxViewModel @Inject constructor(
    private val repository: DocxRepository
) : ViewModel() {

    private val _files = MutableStateFlow<List<String>>(emptyList())
    val files = _files.asStateFlow()

    private val _uiState = MutableStateFlow<DocxUiState>(DocxUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private var openedFile: String? = null

    fun loadFiles(context: Context) {
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) { repository.listDocxFiles(context) }
                _files.value = list
            } catch (e: Exception) {
                Log.e("DocxViewModel", "loadFiles error", e)
            }
        }
    }

    fun openFile(context: Context, filename: String) {
        if (openedFile == filename) return
        openedFile = filename

        viewModelScope.launch {
            _uiState.value = DocxUiState.Loading

            try {
                val doc = withContext(Dispatchers.IO) { repository.getDocxDocument(context, filename) }
                _uiState.value = DocxUiState.Success(doc)
            } catch (e: Exception) {
                Log.e("DocxViewModel", "openFile error", e)
                _uiState.value = DocxUiState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun closeDocument() {
        openedFile = null
        _uiState.value = DocxUiState.Idle
    }
}

