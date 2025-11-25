package com.firsov.rza.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.DocxParagraph

@Composable
fun ParagraphView(paragraph: DocxParagraph) {
    val annotatedString = buildAnnotatedString {
        paragraph.runs.forEach { run ->
            withStyle(
                style = SpanStyle(
                    fontWeight = if (run.bold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (run.italic) FontStyle.Italic else FontStyle.Normal,
                    textDecoration = if (run.underline) TextDecoration.Underline else TextDecoration.None,
                )
            ) {
                append(run.text)
            }
        }
    }
    Text(
        text = annotatedString,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}