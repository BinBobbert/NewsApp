package com.dejevij.newsapp.ui

import android.text.format.DateUtils
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

class RelativeTimeFormatter @Inject constructor(private val clock: Clock) {

    fun format(instant: Instant): String = DateUtils.getRelativeTimeSpanString(
        instant.toEpochMilliseconds(),
        clock.now().toEpochMilliseconds(),
        DateUtils.MINUTE_IN_MILLIS,
    ).toString()
}
