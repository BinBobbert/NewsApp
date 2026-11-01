package com.dejevij.newsapp.model

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
}
