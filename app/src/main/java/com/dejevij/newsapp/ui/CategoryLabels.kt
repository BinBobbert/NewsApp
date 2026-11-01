package com.dejevij.newsapp.ui

import androidx.annotation.StringRes
import com.dejevij.newsapp.R
import com.dejevij.newsapp.model.Category

@get:StringRes
val Category.labelRes: Int
    get() = when (this) {
        Category.BUSINESS -> R.string.category_business
        Category.ENTERTAINMENT -> R.string.category_entertainment
        Category.GENERAL -> R.string.category_general
        Category.HEALTH -> R.string.category_health
        Category.SCIENCE -> R.string.category_science
        Category.SPORTS -> R.string.category_sports
        Category.TECHNOLOGY -> R.string.category_technology
    }
