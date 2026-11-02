package com.dejevij.newsapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dejevij.newsapp.model.Country
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DataStoreCountryRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : CountryRepository {

    override val country: Flow<Country> = dataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { preferences ->
            preferences[COUNTRY_KEY]?.let(Country::fromCode)
                ?: Country.forLocale(Locale.getDefault())
        }
        .distinctUntilChanged()

    override suspend fun setCountry(country: Country) {
        dataStore.edit { preferences -> preferences[COUNTRY_KEY] = country.code }
    }

    private companion object {

        val COUNTRY_KEY = stringPreferencesKey("country")
    }
}
