package com.firsov.rza.data.models

// Контент ячейки таблицы
sealed interface TableCellContent {
    data class Text(val value: String) : TableCellContent
    data class Image(val bytes: ByteArray) : TableCellContent
    data class Formula(val ommlXml: String) : TableCellContent
}

// Простая таблица
typealias SimpleTableRow = List<TableCellContent>
typealias SimpleTable = List<SimpleTableRow>

// Абстрактный блок документа
sealed class DocxBlock
data class DocxText(val text: String) : DocxBlock()
data class DocxImage(val bytes: ByteArray) : DocxBlock()
data class DocxTable(
    val rows: List<List<TableCellContent>>
) : DocxBlock()

data class DocxFormula(val ommlXml: String) : DocxBlock()


// Глава документа
data class Chapter(val title: String, val blocks: List<DocxBlock>)

// Полный документ
data class DocxDocument(
    val fileName: String,
    val chapters: List<Chapter>
)


