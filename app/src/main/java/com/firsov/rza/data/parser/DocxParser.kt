package com.firsov.rza.data.parser

import android.content.Context
import com.firsov.rza.data.models.Chapter
import com.firsov.rza.data.models.DocxBlock
import com.firsov.rza.data.models.DocxTable
import com.firsov.rza.data.models.TableCellContent
import org.apache.poi.xwpf.usermodel.BodyElementType
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable


class DocxParser {

    fun parseAssetDocx(context: Context, fileName: String): List<Chapter> {
        val inputStream = context.assets.open("docs/$fileName")
        val document = XWPFDocument(inputStream)

        val chapters = mutableListOf<Chapter>()
        var currentTitle = "Без названия"
        var currentBlocks = mutableListOf<DocxBlock>()

        for (bodyElement in document.bodyElements) {

            when (bodyElement.elementType) {

                // ================================
                // ПАРАГРАФ
                // ================================
                BodyElementType.PARAGRAPH -> {
                    val para = bodyElement as XWPFParagraph

                    // Заголовок
                    val text = para.text.trim()
                    if (para.style == "Heading1" && text.isNotEmpty()) {
                        if (currentBlocks.isNotEmpty()) {
                            chapters.add(Chapter(currentTitle, currentBlocks))
                            currentBlocks = mutableListOf()
                        }
                        currentTitle = text
                        continue
                    }

                    // Обычный параграф → inline-парсинг
                    val blocks = parseParagraphInline(para)
                    currentBlocks.addAll(blocks)
                }

                // ================================
                // ТАБЛИЦА
                // ================================
                BodyElementType.TABLE -> {
                    val table = bodyElement as XWPFTable
                    val parsedRows = mutableListOf<List<TableCellContent>>()

                    table.rows.forEach { row ->
                        val parsedCells = row.tableCells.map { cell ->
                            parseTableCell(cell).firstOrNull() ?: TableCellContent.Text("")
                        }
                        parsedRows.add(parsedCells)
                    }

                    currentBlocks.add(DocxTable(parsedRows))
                }

                else -> Unit
            }
        }

        // Последняя глава
        if (currentBlocks.isNotEmpty()) {
            chapters.add(Chapter(currentTitle, currentBlocks))
        }

        document.close()
        return chapters
    }
}
