package com.dejevij.newsapp.model

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CountryTest {

    @Test
    fun `fromCode accepts supported codes in any case`() {
        assertEquals("gb", Country.fromCode("GB")?.code)
        assertEquals("nl", Country.fromCode("nl")?.code)
    }

    @Test
    fun `fromCode rejects unsupported and empty codes`() {
        assertNull(Country.fromCode("es"))
        assertNull(Country.fromCode(""))
    }

    @Test
    fun `forLocale uses the locale country when supported`() {
        assertEquals(
            "nl",
            Country.forLocale(Locale.Builder().setLanguage("nl").setRegion("NL").build()).code,
        )
    }

    @Test
    fun `forLocale falls back to the default country`() {
        assertEquals(
            Country.Default,
            Country.forLocale(Locale.Builder().setLanguage("es").setRegion("ES").build()),
        )
        assertEquals(Country.Default, Country.forLocale(Locale.Builder().setLanguage("en").build()))
    }
}
