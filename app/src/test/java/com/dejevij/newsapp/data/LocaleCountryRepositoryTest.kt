package com.dejevij.newsapp.data

import com.dejevij.newsapp.model.Country
import java.util.Locale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class LocaleCountryRepositoryTest {

    private val originalLocale = Locale.getDefault()
    private val repository = LocaleCountryRepository()

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun `uses the country of the default locale`() = runTest {
        Locale.setDefault(Locale.Builder().setLanguage("nl").setRegion("NL").build())

        assertEquals("nl", repository.country.first().code)
    }

    @Test
    fun `falls back to the default country when the locale country is unsupported`() = runTest {
        Locale.setDefault(Locale.Builder().setLanguage("es").setRegion("ES").build())

        assertEquals(Country.Default, repository.country.first())
    }

    @Test
    fun `falls back to the default country when the locale has no country`() = runTest {
        Locale.setDefault(Locale.Builder().setLanguage("en").build())

        assertEquals(Country.Default, repository.country.first())
    }
}
