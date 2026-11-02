package com.dejevij.newsapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailKey(val url: String) : NavKey
