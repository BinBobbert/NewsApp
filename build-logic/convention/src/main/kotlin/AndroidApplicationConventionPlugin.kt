import com.android.build.api.dsl.ApplicationExtension
import com.dejevij.newsapp.buildlogic.AndroidConfig
import com.dejevij.newsapp.buildlogic.configureAndroidDefaults
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("newsapp.code.quality")

        extensions.configure<ApplicationExtension> {
            configureAndroidDefaults()
            defaultConfig.targetSdk = AndroidConfig.TARGET_SDK
        }
    }
}
