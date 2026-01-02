package com.firsov.rza.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import com.firsov.rza.data.models.*
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

    suspend fun getDocxDocument(
        context: Context,
        filename: String
    ): DocxDocument = withContext(Dispatchers.IO) {
        val chapters = parser.parseAssetDocx(context, filename)
        DocxDocument(filename, chapters)
    }

}
