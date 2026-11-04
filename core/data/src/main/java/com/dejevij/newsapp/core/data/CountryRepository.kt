package com.dejevij.newsapp.core.data

import com.dejevij.newsapp.core.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {

    val country: Flow<Country>

    suspend fun setCountry(country: Country)
}
