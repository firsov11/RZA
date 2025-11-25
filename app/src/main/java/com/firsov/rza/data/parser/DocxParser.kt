package com.firsov.rza.data.parser

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import com.firsov.rza.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.UnderlinePatterns
import java.io.InputStream

class DocxParser(private val context: Context) {

    suspend fun parseAssetDocx(filename: String): List<DocxChapter> = withContext(Dispatchers.IO) {
        val input: InputStream = context.assets.open(filename)
        val document = XWPFDocument(input)

        val flatBlocks = mutableListOf<DocxBlock>()

        for (bodyElement in document.bodyElements) {
            when (bodyElement) {
                is org.apache.poi.xwpf.usermodel.XWPFParagraph -> {
                    val p = bodyElement
                    val style = p.style ?: ""
                    if (style.startsWith("Heading")) {
                        val level = style.removePrefix("Heading").toIntOrNull() ?: 1
                        flatBlocks.add(DocxHeading(level, p.text))
                    } else {
                        flatBlocks.add(parseParagraph(p))
                    }
                }
                is org.apache.poi.xwpf.usermodel.XWPFTable -> {
                    flatBlocks.add(parseTable(bodyElement))
                }
            }
        }

        // Добавляем картинки как ленивые
        document.paragraphs.forEach { para ->
            para.runs.forEach { run ->
                run.embeddedPictures.forEach { pic ->
                    flatBlocks.add(DocxImageLazy(pic.pictureData.fileName, pic.pictureData.data))
                }
            }
        }

        // Разделяем на главы
        val chapters = mutableListOf<DocxChapter>()
        var currentTitle = "Глава"
        val currentBlocks = mutableListOf<DocxBlock>()

        for (block in flatBlocks) {
            if (block is DocxHeading) {
                if (currentBlocks.isNotEmpty()) {
                    chapters.add(DocxChapter(currentTitle, currentBlocks.toList()))
                    currentBlocks.clear()
                }
                currentTitle = block.text
            } else {
                currentBlocks.add(block)
            }
        }
        if (currentBlocks.isNotEmpty()) {
            chapters.add(DocxChapter(currentTitle, currentBlocks.toList()))
        }

        document.close()
        chapters
    }

    private fun parseParagraph(p: org.apache.poi.xwpf.usermodel.XWPFParagraph): DocxParagraph {
        val runs = p.runs.map { run ->
            DocxTextRun(
                text = run.text(),
                bold = run.isBold,
                italic = run.isItalic,
                underline = run.underline == UnderlinePatterns.SINGLE
            )
        }
        return DocxParagraph(runs)
    }

    private fun parseTable(table: org.apache.poi.xwpf.usermodel.XWPFTable): DocxTable {
        val rows = table.rows.map { row ->
            row.tableCells.map { it.text }
        }
        return DocxTable(rows)
    }

    suspend fun loadImagesLazy(chapters: List<DocxChapter>) = withContext(Dispatchers.Default) {
        chapters.forEach { chapter ->
            chapter.blocks.forEach { block ->
                if (block is DocxImageLazy && block.bitmap == null) {
                    block.bitmap = BitmapFactory.decodeByteArray(block.bytes, 0, block.bytes.size).asImageBitmap()
                }
            }
        }
    }
}
