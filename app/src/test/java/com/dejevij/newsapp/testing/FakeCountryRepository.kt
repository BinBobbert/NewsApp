package com.dejevij.newsapp.testing

import com.dejevij.newsapp.data.CountryRepository
import com.dejevij.newsapp.model.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCountryRepository(initial: Country = Country.Default) : CountryRepository {

    private val state = MutableStateFlow(initial)

    override val country: Flow<Country> = state

    override suspend fun setCountry(country: Country) {
        state.value = country
    }
}
