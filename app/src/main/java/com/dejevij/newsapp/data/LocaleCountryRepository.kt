package com.dejevij.newsapp.data

import com.dejevij.newsapp.model.Country
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocaleCountryRepository @Inject constructor() : CountryRepository {

    override val country: Flow<Country> = flow {
        emit(Country.fromCode(Locale.getDefault().country) ?: Country.Default)
    }
}
