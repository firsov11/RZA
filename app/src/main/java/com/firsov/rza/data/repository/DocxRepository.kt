package com.firsov.rza.data.repository

import android.content.Context
import com.firsov.rza.data.models.DocxChapter
import com.firsov.rza.data.models.DocxDocument
import com.firsov.rza.data.parser.DocxParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocxRepository @Inject constructor(
    private val parser: DocxParser
) {

    fun listDocxFiles(context: Context): List<String> =
        context.assets.list("")?.filter { it.endsWith(".docx") } ?: emptyList()

    suspend fun getDocxDocument(context: Context, filename: String) =
        DocxDocument(parser.parseAssetDocx(filename))

    // Новый публичный метод для ленивой загрузки картинок
    suspend fun loadImagesLazy(chapters: List<DocxChapter>) {
        parser.loadImagesLazy(chapters)
    }
}

