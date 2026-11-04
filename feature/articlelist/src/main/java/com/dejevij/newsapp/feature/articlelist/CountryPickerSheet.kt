package com.dejevij.newsapp.feature.articlelist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.dejevij.newsapp.core.designsystem.preview.PreviewContainer
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.core.model.Country
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryPickerSheet(
    countries: List<CountryViewData>,
    selectedCountry: Country,
    onCountrySelect: (Country) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
    ) {
        CountryPickerContent(
            countries = countries,
            selectedCountry = selectedCountry,
            onCountrySelect = { country ->
                onCountrySelect(country)
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
            },
        )
    }
}

@Composable
fun CountryPickerContent(
    countries: List<CountryViewData>,
    selectedCountry: Country,
    onCountrySelect: (Country) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        item {
            Text(
                text = stringResource(R.string.country_picker_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = NewsAppTheme.spacing.default),
            )
            Spacer(modifier = Modifier.height(NewsAppTheme.spacing.small))
        }
        items(items = countries, key = { it.country.code }) { country ->
            CountryRow(
                country = country,
                selected = country.country == selectedCountry,
                onClick = { onCountrySelect(country.country) },
            )
        }
    }
}

@Composable
private fun CountryRow(
    country: CountryViewData,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(
                horizontal = NewsAppTheme.spacing.default,
                vertical = NewsAppTheme.spacing.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = country.flag,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.clearAndSetSemantics {},
        )
        Spacer(modifier = Modifier.width(NewsAppTheme.spacing.default))
        Text(
            text = country.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        RadioButton(selected = selected, onClick = null)
    }
}

@PreviewLightDark
@Composable
private fun CountryPickerContentPreview(
    @PreviewParameter(CountryPickerPreviewParameterProvider::class)
    state: CountryPickerPreviewState,
) {
    PreviewContainer(state) {
        CountryPickerContent(
            countries = it.countries,
            selectedCountry = it.selectedCountry,
            onCountrySelect = {},
        )
    }
}
