package com.dejevij.newsapp.core.ui.util

import android.content.ActivityNotFoundException
import android.content.Context
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun Context.openInCustomTab(url: String, @ColorInt toolbarColor: Int): Boolean = try {
    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder().setToolbarColor(toolbarColor).build(),
        )
        .build()
        .launchUrl(this, url.toUri())
    true
} catch (_: ActivityNotFoundException) {
    false
}
