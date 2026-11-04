package com.dejevij.newsapp.core.testing

import kotlin.time.Clock
import kotlin.time.Instant

class FixedClock(private val now: Instant) : Clock {
    override fun now() = now
}
