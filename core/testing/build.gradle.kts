plugins {
    alias(libs.plugins.newsapp.android.library)
}

android {
    namespace = "com.dejevij.newsapp.core.testing"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)
    api(projects.core.network)
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
}
