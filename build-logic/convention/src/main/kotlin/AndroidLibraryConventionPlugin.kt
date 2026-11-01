import com.android.build.api.dsl.LibraryExtension
import com.dejevij.newsapp.buildlogic.configureAndroidDefaults
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("newsapp.code.quality")

        extensions.configure<LibraryExtension> {
            configureAndroidDefaults()
        }
    }
}
