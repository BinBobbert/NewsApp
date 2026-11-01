package com.dejevij.newsapp.ui.articlelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.dejevij.newsapp.model.Country

internal class CountryPickerPreviewParameterProvider :
    PreviewParameterProvider<CountryPickerPreviewState> {
    override val values = sequenceOf(
        CountryPickerPreviewState(
            countries = listOf(
                previewCountry("nl", "Netherlands", "🇳🇱"),
                previewCountry("gb", "United Kingdom", "🇬🇧"),
                previewCountry("us", "United States", "🇺🇸"),
            ),
            selectedCountry = Country.Default,
        ),
    )
}

internal data class CountryPickerPreviewState(
    val countries: List<CountryViewData>,
    val selectedCountry: Country,
)

internal fun previewCountry(code: String, name: String, flag: String) =
    CountryViewData(country = requireNotNull(Country.fromCode(code)), name = name, flag = flag)
