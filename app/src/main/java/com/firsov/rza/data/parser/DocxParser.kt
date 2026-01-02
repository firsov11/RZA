package com.firsov.rza.data.parser

import android.content.Context
import com.firsov.rza.data.models.Chapter
import com.firsov.rza.data.models.DocxBlock
import com.firsov.rza.data.models.DocxTable
import org.apache.poi.xwpf.usermodel.BodyElementType
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable

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



