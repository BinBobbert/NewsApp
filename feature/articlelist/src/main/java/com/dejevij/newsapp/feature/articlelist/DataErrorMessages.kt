package com.dejevij.newsapp.feature.articlelist

import androidx.annotation.StringRes
import com.dejevij.newsapp.core.data.DataError

@get:StringRes
val DataError.messageRes: Int
    get() = when (this) {
        DataError.NoConnection -> R.string.error_no_connection
        DataError.Unauthorized -> R.string.error_unauthorized
        DataError.RateLimited -> R.string.error_rate_limited
        is DataError.Server -> R.string.error_server
        DataError.Unknown -> R.string.error_unknown
    }
