package com.firsov.rza.data.models

import android.graphics.Bitmap

sealed class DocxBlock

data class DocxText(val text: String) : DocxBlock()
data class DocxImage(
    val bytes: ByteArray,
    val bitmap: Bitmap? = null // новое поле для ленивой подгрузки
) : DocxBlock()
data class DocxTable(val rows: SimpleTable) : DocxBlock()

sealed interface TableCellContent {
    data class Text(val value: String) : TableCellContent
    data class Image(val bytes: ByteArray, val bitmap: Bitmap? = null) : TableCellContent
}

typealias SimpleTableRow = List<TableCellContent>
typealias SimpleTable = List<SimpleTableRow>

data class Chapter(val title: String, val blocks: List<DocxBlock>)
data class DocxDocument(val fileName: String, val chapters: List<Chapter>)
