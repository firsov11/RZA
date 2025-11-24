package com.firsov.rza.data.models

import android.graphics.Bitmap

data class DocxDocument(
    val title: String,
    val chapters: List<Chapter>
)

data class Chapter(
    val title: String,
    val paragraphs: List<String>,
    val tables: List<List<List<String>>> = emptyList(), // Таблицы: строки -> ячейки
    val images: List<Bitmap> = emptyList()
)
