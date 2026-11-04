package com.dejevij.newsapp.ui.articlelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.dejevij.newsapp.R
import com.dejevij.newsapp.core.designsystem.component.PrimaryButton
import com.dejevij.newsapp.core.designsystem.component.SecondaryButton
import com.dejevij.newsapp.core.designsystem.component.SelectablePill
import com.dejevij.newsapp.core.designsystem.preview.PreviewContainer
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.model.Category
import com.dejevij.newsapp.ui.labelRes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterSheet(
    selectedCategories: Set<Category>,
    onApply: (Set<Category>) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var draft by rememberSaveable { mutableStateOf(selectedCategories) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
    ) {
        CategoryFilterContent(
            selectedCategories = draft,
            onCategoryToggle = { category ->
                draft = if (category in draft) draft - category else draft + category
            },
            onClear = { draft = emptySet() },
            onApply = {
                onApply(draft)
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
            },
        )
    }
}

@Composable
fun CategoryFilterContent(
    selectedCategories: Set<Category>,
    onCategoryToggle: (Category) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NewsAppTheme.spacing.default)
            .padding(bottom = NewsAppTheme.spacing.default),
    ) {
        Text(
            text = stringResource(R.string.category_filter_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(NewsAppTheme.spacing.default))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(NewsAppTheme.spacing.small)) {
            Category.entries.forEach { category ->
                SelectablePill(
                    text = stringResource(category.labelRes),
                    selected = category in selectedCategories,
                    onClick = { onCategoryToggle(category) },
                )
            }
        }
        Spacer(modifier = Modifier.height(NewsAppTheme.spacing.medium))
        Row(modifier = Modifier.fillMaxWidth()) {
            SecondaryButton(
                text = stringResource(R.string.category_filter_clear),
                onClick = onClear,
                enabled = selectedCategories.isNotEmpty(),
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(NewsAppTheme.spacing.small))
            PrimaryButton(
                text = stringResource(R.string.category_filter_apply),
                onClick = onApply,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryFilterContentPreview(
    @PreviewParameter(CategoryFilterPreviewParameterProvider::class)
    state: CategoryFilterPreviewState,
) {
    PreviewContainer(state) {
        CategoryFilterContent(
            selectedCategories = it.selectedCategories,
            onCategoryToggle = {},
            onClear = {},
            onApply = {},
        )
    }
}
