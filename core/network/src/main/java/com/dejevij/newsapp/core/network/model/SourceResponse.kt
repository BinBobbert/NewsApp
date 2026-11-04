package com.dejevij.newsapp.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SourceResponse(val id: String? = null, val name: String)
