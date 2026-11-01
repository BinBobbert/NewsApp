package com.dejevij.newsapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
    val tiny: Dp = 4.dp,
    val small: Dp = 8.dp,
    val default: Dp = 16.dp,
    val medium: Dp = 24.dp,
    val large: Dp = 32.dp
)

internal val LocalSpacing = staticCompositionLocalOf { Spacing() }
