package com.dejevij.newsapp.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SourceResponse(val id: String? = null, val name: String)
