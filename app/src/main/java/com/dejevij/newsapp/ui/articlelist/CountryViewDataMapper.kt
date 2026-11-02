package com.dejevij.newsapp.ui.articlelist

import com.dejevij.newsapp.model.Country
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

class CountryViewDataMapper @Inject constructor() {

    fun map(countries: List<Country>): List<CountryViewData> {
        val collator = Collator.getInstance()
        return countries
            .map { country ->
                CountryViewData(
                    country = country,
                    name = Locale.Builder().setRegion(
                        country.code.uppercase(),
                    ).build().displayCountry,
                    flag = country.code.uppercase()
                        .map { REGIONAL_INDICATOR_A + (it - 'A') }
                        .joinToString(separator = "") { String(Character.toChars(it)) },
                )
            }
            .sortedWith(compareBy(collator) { it.name })
    }

    private companion object {

        const val REGIONAL_INDICATOR_A = 0x1F1E6
    }
}
