package com.firsov.rza.data.repository

import android.content.Context
import com.firsov.rza.data.models.DocxDocument
import com.firsov.rza.data.parser.DocxParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class DocxRepository @Inject constructor(
    private val parser: DocxParser
) {

    // список DOCX файлов в assets/docs
    fun listDocxFiles(context: Context): List<String> {
        val list = context.assets.list("docs")?.toList() ?: emptyList()
        Log.d("DocxRepository", "Found ${list.size} files")
        return list
    }

    // Получение документа с разбором DOCX
    suspend fun getDocxDocument(context: Context, filename: String): DocxDocument = withContext(Dispatchers.IO) {
        try {
            Log.d("DocxRepository", "Parsing DOCX file: $filename")
            val chapters = parser.parseAssetDocx(context, filename)
            Log.d("DocxRepository", "Parsed ${chapters.size} chapters")
            DocxDocument(filename, chapters)
        } catch (e: Exception) {
            Log.e("DocxRepository", "Failed to parse DOCX: $filename", e)
            throw e
        }
    }

    // подгрузка изображений (у тебя пока пусто)
    suspend fun loadImagesLazy(chapters: List<com.firsov.rza.data.models.Chapter>) = withContext(Dispatchers.IO) {
        try {
            Log.d("DocxRepository", "Lazy loading images for ${chapters.size} chapters")
            // если когда-то захочешь загружать изображения из assets, делай это здесь
        } catch (e: Exception) {
            Log.e("DocxRepository", "Failed to load images", e)
        }
    }
}
