plugins {
    alias(libs.plugins.newsapp.android.feature)
}

android {
    namespace = "com.dejevij.newsapp.feature.articlelist"
}

dependencies {
    implementation(projects.core.common)

    testImplementation(libs.coil.compose)
    testImplementation(libs.coil.test)
}
