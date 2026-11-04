package com.dejevij.newsapp.core.testing

import com.dejevij.newsapp.core.data.CountryRepository
import com.dejevij.newsapp.core.model.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCountryRepository(initial: Country = Country.Default) : CountryRepository {

    private val state = MutableStateFlow(initial)

    override val country: Flow<Country> = state

    override suspend fun setCountry(country: Country) {
        state.value = country
    }
}
