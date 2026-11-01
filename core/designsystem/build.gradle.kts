plugins {
    alias(libs.plugins.newsapp.android.library)
    alias(libs.plugins.newsapp.android.compose)
}

android {
    namespace = "com.dejevij.newsapp.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
}
