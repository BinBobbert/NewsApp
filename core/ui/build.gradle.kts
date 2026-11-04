plugins {
    alias(libs.plugins.newsapp.android.library)
    alias(libs.plugins.newsapp.android.compose)
    alias(libs.plugins.newsapp.hilt)
}

android {
    namespace = "com.dejevij.newsapp.core.ui"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(libs.androidx.browser)
    implementation(libs.coil.compose)

    testImplementation(projects.core.testing)
}
