plugins {
    alias(libs.plugins.newsapp.android.application)
    alias(libs.plugins.newsapp.android.compose)
    alias(libs.plugins.newsapp.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.dejevij.newsapp"

    defaultConfig {
        applicationId = "com.dejevij.newsapp"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.dejevij.newsapp.HiltTestRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
}

kover {
    reports {
        filters {
            excludes {
                classes("com.dejevij.newsapp.MainActivity*", "com.dejevij.newsapp.NewsApplication*")
            }
        }
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.network)
    implementation(projects.feature.articledetail)
    implementation(projects.feature.articlelist)

    kover(projects.core.common)
    kover(projects.core.data)
    kover(projects.core.designsystem)
    kover(projects.core.model)
    kover(projects.core.network)
    kover(projects.core.ui)
    kover(projects.feature.articledetail)
    kover(projects.feature.articlelist)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.compose.ui)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    androidTestImplementation(projects.core.data)
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}
