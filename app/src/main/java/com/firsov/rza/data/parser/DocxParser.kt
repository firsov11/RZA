package com.firsov.rza.data.parser

import android.content.Context
import com.firsov.rza.data.models.*
import org.apache.poi.xwpf.usermodel.BodyElementType
import org.apache.poi.xwpf.usermodel.XWPFDocument

class DocxParser {

    fun parseAssetDocx(context: Context, fileName: String): List<Chapter> {
        val inputStream = context.assets.open("docs/$fileName")
        val document = XWPFDocument(inputStream)

        val chapters = mutableListOf<Chapter>()
        var currentTitle = "Без названия"
        var currentBlocks = mutableListOf<DocxBlock>()

        for (bodyElement in document.bodyElements) {

            when (bodyElement.elementType) {

                BodyElementType.PARAGRAPH -> {
                    val para = bodyElement as org.apache.poi.xwpf.usermodel.XWPFParagraph
                    val text = para.text.trim()

                    // 📌 новый заголовок (Heading1)
                    if (para.style == "Heading1" && text.isNotEmpty()) {
                        // сохраняем предыдущую главу
                        if (currentBlocks.isNotEmpty()) {
                            chapters.add(Chapter(currentTitle, currentBlocks))
                            currentBlocks = mutableListOf()
                        }
                        currentTitle = text
                        continue
                    }

                    // текст
                    if (text.isNotEmpty()) {
                        currentBlocks.add(DocxText(text))
                    }

                    // картинки
                    para.runs.forEach { run ->
                        run.embeddedPictures.forEach { pic ->
                            currentBlocks.add(DocxImage(pic.pictureData.data))
                        }
                    }
                }

                BodyElementType.TABLE -> {
                    val table = bodyElement as org.apache.poi.xwpf.usermodel.XWPFTable
                    val parsedRows = mutableListOf<SimpleTableRow>()

                    table.rows.forEach { row ->
                        val parsedCells = row.tableCells.map { cell ->

                            val blocks = mutableListOf<TableCellContent>()

                            val cellText = cell.paragraphs.joinToString(" ") {
                                it.runs.joinToString("") { r -> r.text().orEmpty() }
                            }.trim()

                            if (cellText.isNotEmpty()) {
                                blocks.add(TableCellContent.Text(cellText))
                            }

                            cell.paragraphs.forEach { p ->
                                p.runs.forEach { r ->
                                    r.embeddedPictures.forEach { pic ->
                                        blocks.add(TableCellContent.Image(pic.pictureData.data))
                                    }
                                }
                            }

                            if (blocks.isEmpty()) TableCellContent.Text("") else blocks.first()
                        }

                        parsedRows.add(parsedCells)
                    }

                    currentBlocks.add(DocxTable(parsedRows))
                }

                else -> Unit
            }
        }

        // добавляем последнюю главу
        if (currentBlocks.isNotEmpty()) {
            chapters.add(Chapter(currentTitle, currentBlocks))
        }

        document.close()
        return chapters
    }
}
