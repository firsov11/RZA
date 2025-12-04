package com.firsov.rza.data.parser

import com.firsov.rza.data.models.DocxBlock
import com.firsov.rza.data.models.DocxFormula
import com.firsov.rza.data.models.DocxImage
import com.firsov.rza.data.models.DocxText
import com.firsov.rza.data.models.TableCellContent
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTableCell

fun parseParagraphInline(p: XWPFParagraph): List<DocxBlock> {
    val result = mutableListOf<DocxBlock>()
    val textBuffer = StringBuilder()

    fun flushText() {
        if (textBuffer.isNotEmpty()) {
            result.add(DocxText(textBuffer.toString()))
            textBuffer.clear()
        }
    }

    p.runs.forEach { run ->
        val xml = run.ctr.toString()

        // Формулы OMML
        val ommls = extractOmmls(xml)
        if (ommls.isNotEmpty()) {
            flushText()
            ommls.forEach { result.add(DocxFormula(it)) }
            return@forEach
        }

        // Картинки
        val pics = run.embeddedPictures
        if (pics.isNotEmpty()) {
            flushText()
            pics.forEach { result.add(DocxImage(it.pictureData.data)) }
            return@forEach
        }

        // Текст
        val t = run.text()
        if (!t.isNullOrEmpty()) {
            textBuffer.append(t)
        }
    }

    // Добавляем остаток текста
    flushText()
    return result
}


fun parseTableCell(cell: XWPFTableCell): List<TableCellContent> {
    val blocks = mutableListOf<TableCellContent>()
    val textBuffer = StringBuilder()

    fun flushText() {
        if (textBuffer.isNotEmpty()) {
            blocks.add(TableCellContent.Text(textBuffer.toString()))
            textBuffer.clear()
        }
    }

    cell.paragraphs.forEach { p ->
        p.runs.forEach { r ->
            val xml = r.ctr.toString()

            // OMML
            val ommls = extractOmmls(xml)
            if (ommls.isNotEmpty()) {
                flushText()
                ommls.forEach { blocks.add(TableCellContent.Formula(it)) }
                return@forEach
            }

            // Картинки
            val pics = r.embeddedPictures
            if (pics.isNotEmpty()) {
                flushText()
                pics.forEach { blocks.add(TableCellContent.Image(it.pictureData.data)) }
                return@forEach
            }

            // Обычный текст
            val t = r.text()
            if (!t.isNullOrEmpty()) {
                textBuffer.append(t)
            }
        }
    }

    flushText()

    return if (blocks.isEmpty())
        listOf(TableCellContent.Text(""))
    else blocks
}


/**
 * Извлекает все OMML формулы из run XML
 */
fun extractOmmls(xml: String): List<String> {
    val result = mutableListOf<String>()
    var start = 0

    while (true) {
        val start1 = xml.indexOf("<m:oMath", start)
        val start2 = xml.indexOf("<m:oMathPara", start)

        val s = when {
            start1 >= 0 && (start1 < start2 || start2 < 0) -> start1
            start2 >= 0 -> start2
            else -> break
        }

        val endTag = if (xml.indexOf("<m:oMathPara", s) >= 0) "</m:oMathPara>" else "</m:oMath>"
        val e = xml.indexOf(endTag, s)
        if (e < 0) break

        result.add(xml.substring(s, e + endTag.length))
        start = e + endTag.length
    }

    return result
}
