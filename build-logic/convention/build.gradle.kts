plugins {
    `kotlin-dsl`
}

group = "com.dejevij.newsapp.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.kover.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.newsapp.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.newsapp.android.compose.get().pluginId
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.newsapp.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("codeQuality") {
            id = libs.plugins.newsapp.code.quality.get().pluginId
            implementationClass = "CodeQualityConventionPlugin"
        }
    }
}
