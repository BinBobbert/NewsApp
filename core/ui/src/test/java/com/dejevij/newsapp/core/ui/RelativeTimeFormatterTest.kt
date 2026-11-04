package com.dejevij.newsapp.core.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.core.testing.FixedClock
import kotlin.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RelativeTimeFormatterTest {

    private val formatter = RelativeTimeFormatter(FixedClock(Instant.parse("2026-11-01T12:00:00Z")))

    @Test
    fun `formats minutes ago`() {
        assertEquals("5 minutes ago", formatter.format(Instant.parse("2026-11-01T11:55:00Z")))
    }

    @Test
    fun `formats hours ago`() {
        assertEquals("2 hours ago", formatter.format(Instant.parse("2026-11-01T10:00:00Z")))
    }
}
