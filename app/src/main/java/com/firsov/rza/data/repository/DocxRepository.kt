package com.firsov.rza.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import com.firsov.rza.data.models.Chapter
import com.firsov.rza.data.models.DocxDocument
import com.firsov.rza.data.models.DocxImage
import com.firsov.rza.data.models.DocxTable
import com.firsov.rza.data.models.TableCellContent
import com.firsov.rza.data.parser.DocxParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocxRepository @Inject constructor(
    private val parser: DocxParser
) {

    fun listDocxFiles(context: Context): List<String> =
        context.assets.list("docs")?.toList() ?: emptyList()

    suspend fun getDocxDocument(context: Context, filename: String): DocxDocument =
        withContext(Dispatchers.IO) {
            val chapters = parser.parseAssetDocx(context, filename)
            DocxDocument(filename, chapters)
        }

    // лениво декодируем картинки
    suspend fun loadImagesLazy(chapters: List<Chapter>): List<Chapter> = withContext(Dispatchers.IO) {
        chapters.map { chapter ->
            val newBlocks = chapter.blocks.map { block ->
                when (block) {
                    is DocxImage -> {
                        val bmp = BitmapFactory.decodeByteArray(block.bytes, 0, block.bytes.size)
                        block.copy(bitmap = bmp)
                    }
                    is DocxTable -> {
                        val newRows = block.rows.map { row ->
                            row.map { cell ->
                                when (cell) {
                                    is TableCellContent.Image -> {
                                        val bmp = BitmapFactory.decodeByteArray(cell.bytes, 0, cell.bytes.size)
                                        cell.copy(bitmap = bmp)
                                    }
                                    else -> cell
                                }
                            }
                        }
                        block.copy(rows = newRows)
                    }
                    else -> block
                }
            }
            chapter.copy(blocks = newBlocks)
        }
    }
}
