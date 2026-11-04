plugins {
    alias(libs.plugins.newsapp.android.library)
}

android {
    namespace = "com.dejevij.newsapp.core.model"
}

dependencies {
    testImplementation(libs.junit)
}
