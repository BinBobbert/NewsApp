import java.util.Properties

plugins {
    alias(libs.plugins.newsapp.android.library)
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
            },
    )
    .getOrElse("")

android {
    namespace = "com.dejevij.newsapp.core.network"

    defaultConfig {
        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
        buildConfigField("String", "NEWS_API_BASE_URL", "\"https://newsapi.org/\"")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.model)
    api(libs.retrofit)
    api(libs.okhttp)
    api(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    debugImplementation(libs.okhttp.logging.interceptor)

    testImplementation(projects.core.testing)
    testImplementation(libs.okhttp.mockwebserver)
}
