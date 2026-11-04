plugins {
    alias(libs.plugins.newsapp.android.library)
    alias(libs.plugins.newsapp.hilt)
}

android {
    namespace = "com.dejevij.newsapp.core.data"
}

dependencies {
    api(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.network)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(projects.core.testing)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.room.runtime)
    testImplementation(libs.robolectric)
}
