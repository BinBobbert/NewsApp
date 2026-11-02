package com.dejevij.newsapp.data

import com.dejevij.newsapp.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {

    val country: Flow<Country>

    suspend fun setCountry(country: Country)
}
