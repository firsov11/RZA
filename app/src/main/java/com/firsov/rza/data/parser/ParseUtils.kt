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
    val ommlBuffer = StringBuilder()  // Буфер для объединения формул, которые могут быть в нескольких run

    fun flushText() {
        if (textBuffer.isNotEmpty()) {
            result.add(DocxText(textBuffer.toString()))
            textBuffer.clear()
        }
    }

    fun flushOmml() {
        if (ommlBuffer.isNotEmpty()) {
            result.add(DocxFormula(ommlBuffer.toString()))
            ommlBuffer.clear()
        }
    }

    p.runs.forEach { run ->
        val xml = run.ctr.toString()

        // Ищем формулы OMML в run
        val ommls = extractOmmls(xml)
        if (ommls.isNotEmpty()) {
            flushText()
            ommls.forEach { omml ->
                ommlBuffer.append(omml)  // Объединяем в один блок, если формула разбита
            }
            flushOmml()
            return@forEach
        }

        // Картинки
        val pics = run.embeddedPictures
        if (pics.isNotEmpty()) {
            flushText()
            flushOmml()
            pics.forEach { pic ->
                result.add(DocxImage(pic.pictureData.data))
            }
            return@forEach
        }

        // Обычный текст
        val t = run.text()
        if (!t.isNullOrEmpty()) {
            flushOmml()
            textBuffer.append(t)
        }
    }

    // Добавляем остатки
    flushText()
    flushOmml()

    return result
}


/**
 * Извлекает все OMML формулы из XML run
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

        // Выбираем правильный endTag
        val endTag = if (xml.indexOf("<m:oMathPara", s) >= 0) "</m:oMathPara>" else "</m:oMath>"
        val e = xml.indexOf(endTag, s)
        if (e < 0) break

        result.add(xml.substring(s, e + endTag.length))
        start = e + endTag.length
    }

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
