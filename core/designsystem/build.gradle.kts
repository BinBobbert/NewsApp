plugins {
    alias(libs.plugins.newsapp.android.library)
    alias(libs.plugins.newsapp.android.compose)
}

android {
    namespace = "com.dejevij.newsapp.core.designsystem"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { test ->
                // Robolectric's API 36+ runtime reads JDK internals that Java modules hide by default.
                test.jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED")
            }
        }
    }
}

dependencies {
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)

    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.espresso.core)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
}
