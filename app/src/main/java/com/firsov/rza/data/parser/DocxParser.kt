package com.firsov.rza.data.parser

import android.content.Context
import android.graphics.BitmapFactory
import com.firsov.rza.data.models.Chapter
import com.firsov.rza.data.models.DocxDocument
import org.apache.poi.xwpf.usermodel.XWPFDocument
import android.graphics.Bitmap

class DocxParser {

    fun parse(context: Context, filename: String): DocxDocument {
        val inputStream = context.assets.open(filename)
        val document = XWPFDocument(inputStream)

        val chapters = mutableListOf<Chapter>()
        var currentTitle = "Introduction"
        var paragraphs = mutableListOf<String>()
        var tables = mutableListOf<List<List<String>>>()
        var images = mutableListOf<Bitmap>()

        // Парсим тело документа
        document.bodyElements.forEach { elem ->
            when (elem) {
                is org.apache.poi.xwpf.usermodel.XWPFParagraph -> {
                    val text = elem.text.trim()
                    if (text.startsWith("Глава") || text.startsWith("Chapter")) {
                        // Сохраняем предыдущую главу
                        if (paragraphs.isNotEmpty() || tables.isNotEmpty() || images.isNotEmpty()) {
                            chapters.add(
                                Chapter(
                                    title = currentTitle,
                                    paragraphs = paragraphs.toList(),
                                    tables = tables.toList(),
                                    images = images.toList()
                                )
                            )
                        }
                        // Начинаем новую главу
                        currentTitle = text
                        paragraphs = mutableListOf()
                        tables = mutableListOf()
                        images = mutableListOf()
                    } else if (text.isNotEmpty()) {
                        paragraphs.add(text)
                    }
                }
                is org.apache.poi.xwpf.usermodel.XWPFTable -> {
                    val tableData = elem.rows.map { row ->
                        row.tableCells.map { it.text }
                    }
                    tables.add(tableData)
                }
            }
        }

        // Добавляем последнюю главу
        if (paragraphs.isNotEmpty() || tables.isNotEmpty() || images.isNotEmpty()) {
            chapters.add(
                Chapter(
                    title = currentTitle,
                    paragraphs = paragraphs,
                    tables = tables,
                    images = images
                )
            )
        }

        // Добавляем все изображения документа в последнюю главу
        document.allPictures.forEach { pictureData ->
            val bitmap = BitmapFactory.decodeByteArray(pictureData.data, 0, pictureData.data.size)
            if (chapters.isNotEmpty()) {
                val last = chapters.last()
                val updatedImages = last.images + bitmap
                chapters[chapters.lastIndex] = last.copy(images = updatedImages)
            }
        }

        return DocxDocument(
            title = filename,
            chapters = chapters
        )
    }
}


