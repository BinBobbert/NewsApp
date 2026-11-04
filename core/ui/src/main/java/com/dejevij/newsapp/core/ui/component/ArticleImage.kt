package com.dejevij.newsapp.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun ArticleImage(imageUrl: String, modifier: Modifier = Modifier) {
    var isError by rememberSaveable(imageUrl) { mutableStateOf(false) }
    if (isError) return

    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        onError = { isError = true },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(IMAGE_ASPECT_RATIO)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

private const val IMAGE_ASPECT_RATIO = 16f / 9f
