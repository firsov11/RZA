package com.firsov.rza.data.parser

import android.content.Context
import com.firsov.rza.data.models.Chapter
import com.firsov.rza.data.models.DocxBlock
import com.firsov.rza.data.models.DocxImage
import com.firsov.rza.data.models.DocxTable
import com.firsov.rza.data.models.DocxText
import com.firsov.rza.data.models.SimpleTable
import com.firsov.rza.data.models.TableCellContent
import org.apache.poi.xwpf.usermodel.BodyElementType
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell

class DocxParser {

    fun parseAssetDocx(context: Context, fileName: String): List<Chapter> {
        context.assets.open("docs/$fileName").use { input ->
            val document = XWPFDocument(input)

            val chapters = mutableListOf<Chapter>()
            var currentTitle = "Без названия"
            var currentBlocks = mutableListOf<DocxBlock>()

            document.bodyElements.forEach { bodyElement ->
                when (bodyElement.elementType) {

                    BodyElementType.PARAGRAPH -> {
                        val para = bodyElement as XWPFParagraph
                        val text = para.text.trim()

                        if (para.style == "Heading1" && text.isNotEmpty()) {
                            if (currentBlocks.isNotEmpty()) {
                                chapters += Chapter(currentTitle, currentBlocks)
                                currentBlocks = mutableListOf()
                            }
                            currentTitle = text
                        } else {
                            currentBlocks += parseParagraphInline(para)
                        }
                    }

                    BodyElementType.TABLE -> {
                        val table = bodyElement as XWPFTable
                        currentBlocks += DocxTable(parseTable(table))
                    }

                    else -> Unit
                }
            }

            if (currentBlocks.isNotEmpty()) {
                chapters += Chapter(currentTitle, currentBlocks)
            }

            return chapters
        }
    }
}


fun parseParagraphInline(p: XWPFParagraph): List<DocxBlock> {
    val result = mutableListOf<DocxBlock>()
    val textBuffer = StringBuilder()

    fun flushText() {
        if (textBuffer.isNotEmpty()) {
            result += DocxText(textBuffer.toString())
            textBuffer.clear()
        }
    }

    p.runs.forEach { run ->
        val pics = run.embeddedPictures

        if (pics.isNotEmpty()) {
            flushText()
            pics.forEach { pic ->
                result += DocxImage(bytes = pic.pictureData.data)
            }
        } else {
            run.text()?.let { textBuffer.append(it) }
        }
    }

    flushText()
    return result
}



fun parseTable(table: XWPFTable): SimpleTable {
    return table.rows.map { row ->
        row.tableCells.map { cell ->
            parseSingleTableCell(cell)
        }
    }
}

fun parseSingleTableCell(cell: XWPFTableCell): TableCellContent {
    val textBuffer = StringBuilder()

    cell.paragraphs.forEach { p ->
        p.runs.forEach { r ->

            val pics = r.embeddedPictures
            if (pics.isNotEmpty()) {
                return TableCellContent.Image(
                    bytes = pics.first().pictureData.data
                )
            }

            r.text()?.let { textBuffer.append(it) }
        }
    }

    return TableCellContent.Text(textBuffer.toString())
}

