package com.dejevij.newsapp.ui.articlelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.dejevij.newsapp.model.Category

internal class CategoryFilterPreviewParameterProvider :
    PreviewParameterProvider<CategoryFilterPreviewState> {
    override val values = sequenceOf(
        CategoryFilterPreviewState(selectedCategories = emptySet()),
        CategoryFilterPreviewState(
            selectedCategories = setOf(Category.SPORTS, Category.TECHNOLOGY),
        ),
    )
}

internal data class CategoryFilterPreviewState(val selectedCategories: Set<Category>)
