import com.android.build.api.dsl.CommonExtension
import com.dejevij.newsapp.buildlogic.libs
import com.dejevij.newsapp.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        extensions.getByType<CommonExtension>().apply {
            buildFeatures.compose = true
            testOptions.unitTests.isIncludeAndroidResources = true
            testOptions.unitTests.all { test ->
                // Robolectric's API 36+ runtime reads JDK internals that Java modules hide by default.
                test.jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED")
            }
        }

        dependencies {
            val bom = platform(libs.library("androidx-compose-bom"))
            add("implementation", bom)
            add("androidTestImplementation", bom)
            add("implementation", libs.library("androidx-compose-ui-tooling-preview"))
            add("debugImplementation", libs.library("androidx-compose-ui-tooling"))
            add("debugImplementation", libs.library("androidx-compose-ui-test-manifest"))

            add("testImplementation", bom)
            add("testImplementation", libs.library("androidx-compose-ui-test-junit4"))
            add("testImplementation", libs.library("androidx-espresso-core"))
            add("testImplementation", libs.library("androidx-junit"))
            add("testImplementation", libs.library("junit"))
            add("testImplementation", libs.library("robolectric"))
        }
    }
}
