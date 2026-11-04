import com.dejevij.newsapp.buildlogic.libs
import com.dejevij.newsapp.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("newsapp.android.library")
        pluginManager.apply("newsapp.android.compose")
        pluginManager.apply("newsapp.hilt")

        dependencies {
            add("implementation", project(":core:data"))
            add("implementation", project(":core:designsystem"))
            add("implementation", project(":core:model"))
            add("implementation", project(":core:ui"))
            add("implementation", libs.library("androidx-lifecycle-runtime-compose"))
            add("implementation", libs.library("androidx-hilt-lifecycle-viewmodel-compose"))

            add("testImplementation", project(":core:testing"))
            add("testImplementation", libs.library("turbine"))
        }
    }
}
