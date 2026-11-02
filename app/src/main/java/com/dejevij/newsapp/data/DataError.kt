package com.dejevij.newsapp.data

sealed interface DataError {

    data object NoConnection : DataError

    data object Unauthorized : DataError

    data object RateLimited : DataError

    data class Server(val code: Int) : DataError

    data object Unknown : DataError
}
