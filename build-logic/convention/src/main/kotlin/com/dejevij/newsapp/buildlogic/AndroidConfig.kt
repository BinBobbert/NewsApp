package com.dejevij.newsapp.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

internal object AndroidConfig {
    const val COMPILE_SDK = 37
    const val MIN_SDK = 24
    const val TARGET_SDK = 37
    val javaVersion = JavaVersion.VERSION_11
}

internal fun CommonExtension.configureAndroidDefaults() {
    compileSdk {
        version = release(AndroidConfig.COMPILE_SDK)
    }
    defaultConfig.apply {
        minSdk = AndroidConfig.MIN_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions.apply {
        sourceCompatibility = AndroidConfig.javaVersion
        targetCompatibility = AndroidConfig.javaVersion
    }
}
