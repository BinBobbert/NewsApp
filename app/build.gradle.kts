import java.util.Properties

plugins {
    alias(libs.plugins.newsapp.android.application)
    alias(libs.plugins.newsapp.android.compose)
    alias(libs.plugins.newsapp.hilt)
    alias(libs.plugins.kotlin.serialization)
}

val newsApiKey: String = providers.environmentVariable("NEWS_API_KEY")
    .orElse(
        providers.fileContents(rootProject.layout.projectDirectory.file("local.properties"))
            .asText
            .map { text ->
                val properties = Properties().apply { load(text.reader()) }
                properties.getProperty("NEWS_API_KEY").orEmpty()
            }
    )
    .getOrElse("")

android {
    namespace = "com.dejevij.newsapp"

    defaultConfig {
        applicationId = "com.dejevij.newsapp"
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
        buildConfigField("String", "NEWS_API_BASE_URL", "\"https://newsapi.org/\"")
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

kover {
    reports {
        filters {
            excludes {
                packages("com.dejevij.newsapp.di")
                classes("com.dejevij.newsapp.MainActivity*", "com.dejevij.newsapp.NewsApplication*")
            }
        }
    }
}

dependencies {
    implementation(projects.core.designsystem)
    kover(projects.core.designsystem)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
