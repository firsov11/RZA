package com.firsov.rza.data.repository

import android.content.Context
import com.firsov.rza.data.models.DocxDocument
import com.firsov.rza.data.parser.DocxParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocxRepository @Inject constructor(
    private val parser: DocxParser
) {

    fun listDocxFiles(context: Context): List<String> {
        return context.assets.list("docs")?.toList() ?: emptyList()
    }

    suspend fun getDocxDocument(context: Context, filename: String): DocxDocument {
        val chapters = parser.parseAssetDocx(context, filename)
        return DocxDocument(filename, chapters)
    }

    fun loadImagesLazy(chapters: List<com.firsov.rza.data.models.Chapter>) {
        // изображения уже подгружены парсером
    }
}
