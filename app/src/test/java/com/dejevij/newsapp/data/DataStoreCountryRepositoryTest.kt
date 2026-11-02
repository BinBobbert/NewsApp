package com.dejevij.newsapp.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dejevij.newsapp.model.Country
import java.io.File
import java.util.Locale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreCountryRepositoryTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val originalLocale = Locale.getDefault()

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun `defaults to the locale country when nothing is stored`() = runTest {
        Locale.setDefault(Locale.Builder().setLanguage("nl").setRegion("NL").build())

        assertEquals("nl", repository().country.first().code)
    }

    @Test
    fun `returns the stored country after setCountry`() = runTest {
        val repository = repository()
        val japan = requireNotNull(Country.fromCode("jp"))

        repository.setCountry(japan)

        assertEquals(japan, repository.country.first())
    }

    @Test
    fun `ignores a stored code that is no longer supported`() = runTest {
        Locale.setDefault(Locale.US)
        val dataStore = dataStore()
        dataStore.edit { it[stringPreferencesKey("country")] = "xx" }

        assertEquals(Country.Default, DataStoreCountryRepository(dataStore).country.first())
    }

    private fun TestScope.repository() = DataStoreCountryRepository(dataStore())

    private fun TestScope.dataStore() = PreferenceDataStoreFactory.create(
        scope = backgroundScope,
        produceFile = { File(temporaryFolder.root, "settings.preferences_pb") },
    )
}
