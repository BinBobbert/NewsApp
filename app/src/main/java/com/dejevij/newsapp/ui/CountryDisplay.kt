package com.dejevij.newsapp.ui

import com.dejevij.newsapp.model.Country
import java.util.Locale

private const val REGIONAL_INDICATOR_A = 0x1F1E6

val Country.displayName: String
    get() = Locale.Builder().setRegion(code.uppercase()).build().displayCountry

val Country.flagEmoji: String
    get() = code.uppercase().map { REGIONAL_INDICATOR_A + (it - 'A') }
        .joinToString(separator = "") { String(Character.toChars(it)) }
