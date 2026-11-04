package com.dejevij.newsapp.core.data

sealed interface RefreshResult {

    data object Success : RefreshResult

    data class Failure(val error: DataError) : RefreshResult
}
