package com.dejevij.newsapp.data

sealed interface RefreshResult {

    data object Success : RefreshResult

    data class Failure(val error: DataError) : RefreshResult
}

sealed interface DataError {

    data object NoConnection : DataError

    data object Unauthorized : DataError

    data object RateLimited : DataError

    data class Server(val code: Int) : DataError

    data object Unknown : DataError
}
