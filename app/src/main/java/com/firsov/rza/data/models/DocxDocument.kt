package com.firsov.rza.data.models

import androidx.compose.ui.graphics.ImageBitmap

sealed interface DocxBlock

data class DocxTextRun(
    val text: String,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false
)

data class DocxParagraph(
    val runs: List<DocxTextRun>
) : DocxBlock

data class DocxTable(
    val rows: List<List<String>>
) : DocxBlock

data class DocxImageLazy(
    val name: String,
    val bytes: ByteArray,
    var bitmap: ImageBitmap? = null
) : DocxBlock {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DocxImageLazy) return false
        if (name != other.name) return false
        if (!bytes.contentEquals(other.bytes)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}

data class DocxHeading(
    val level: Int,
    val text: String
) : DocxBlock

data class DocxChapter(
    val title: String,
    val blocks: List<DocxBlock>
)

data class DocxDocument(
    val chapters: List<DocxChapter>
)
