package com.dejevij.newsapp.core.model

import java.util.Locale

@JvmInline
value class Country private constructor(val code: String) {

    companion object {

        val Default = Country("us")

        // Countries supported by NewsAPI's top-headlines endpoint (ISO 3166-1 alpha-2).
        private val supportedCodes = setOf(
            "ae", "ar", "at", "au", "be", "bg", "br", "ca", "ch", "cn", "co", "cu", "cz", "de",
            "eg", "fr", "gb", "gr", "hk", "hu", "id", "ie", "il", "in", "it", "jp", "kr", "lt",
            "lv", "ma", "mx", "my", "ng", "nl", "no", "nz", "ph", "pl", "pt", "ro", "rs", "ru",
            "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us", "ve", "za",
        )

        val all: List<Country> = supportedCodes.map(::Country)

        fun forLocale(locale: Locale): Country = fromCode(locale.country) ?: Default

        fun fromCode(code: String): Country? =
            code.lowercase().takeIf { it in supportedCodes }?.let(::Country)
    }
}
