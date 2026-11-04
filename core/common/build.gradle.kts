plugins {
    alias(libs.plugins.newsapp.android.library)
    alias(libs.plugins.newsapp.hilt)
}

android {
    namespace = "com.dejevij.newsapp.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
