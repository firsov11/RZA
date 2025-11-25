package com.firsov.rza.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firsov.rza.data.models.DocxHeading

@Composable
fun HeadingView(heading: DocxHeading) {
    val style = when (heading.level) {
        1 -> MaterialTheme.typography.headlineLarge
        2 -> MaterialTheme.typography.headlineMedium
        else -> MaterialTheme.typography.headlineSmall
    }
    Text(
        text = heading.text,
        style = style,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}