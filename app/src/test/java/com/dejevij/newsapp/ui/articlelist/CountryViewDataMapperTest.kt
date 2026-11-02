package com.dejevij.newsapp.ui.articlelist

import com.dejevij.newsapp.model.Country
import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CountryViewDataMapperTest {

    private val originalLocale = Locale.getDefault()
    private val mapper = CountryViewDataMapper()

    @Before
    fun setUp() {
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun `maps the localised name and flag`() {
        val netherlands = requireNotNull(Country.fromCode("nl"))

        assertEquals(
            listOf(CountryViewData(country = netherlands, name = "Netherlands", flag = "🇳🇱")),
            mapper.map(listOf(netherlands)),
        )
    }

    @Test
    fun `always sorts countries by localised name`() {
        val countries = listOf("us", "nl", "au").map { requireNotNull(Country.fromCode(it)) }

        assertEquals(
            listOf("Australia", "Netherlands", "United States"),
            mapper.map(countries).map { it.name },
        )
    }
}
