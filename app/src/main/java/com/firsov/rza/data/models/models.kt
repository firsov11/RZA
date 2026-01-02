package com.firsov.rza.data.models

import androidx.compose.ui.graphics.ImageBitmap

/* ---------- DOCX BLOCKS ---------- */

sealed class DocxBlock

data class DocxText(
    val text: String
) : DocxBlock()

data class DocxImage(
    val bytes: ByteArray,
    val image: ImageBitmap? = null
) : DocxBlock()

data class DocxTable(
    val table: DocxTableModel
) : DocxBlock()

/* ---------- TABLE MODEL ---------- */

data class DocxTableModel(
    val rows: List<TableRow>,
    val columnCount: Int
)

data class TableRow(
    val cells: List<TableCell?> // теперь null допустим
)


/**
 * ОДНА логическая ячейка Word
 * (НЕ дублируется по span'ам)
 */
data class TableCell(
    val content: TableCellContent,
    val colSpan: Int = 1,
    val rowSpan: Int = 1
)

/* ---------- CELL CONTENT ---------- */

sealed interface TableCellContent {

    data class Text(
        val value: String
    ) : TableCellContent

    data class Image(
        val bytes: ByteArray,
        val image: ImageBitmap? = null
    ) : TableCellContent
}

/* ---------- DOCUMENT ---------- */

data class Chapter(
    val title: String,
    val blocks: List<DocxBlock>
)

data class DocxDocument(
    val fileName: String,
    val chapters: List<Chapter>
)
