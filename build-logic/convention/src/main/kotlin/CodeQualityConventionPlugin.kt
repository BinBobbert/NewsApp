import com.dejevij.newsapp.buildlogic.libs
import com.dejevij.newsapp.buildlogic.library
import com.dejevij.newsapp.buildlogic.version
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class CodeQualityConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")
        pluginManager.apply("io.gitlab.arturbosch.detekt")
        pluginManager.apply("org.jetbrains.kotlinx.kover")

        extensions.configure<KtlintExtension> {
            version.set(libs.version("ktlint"))
            android.set(true)
        }

        extensions.configure<DetektExtension> {
            buildUponDefaultConfig = true
            config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        }

        extensions.configure<KoverProjectExtension> {
            reports {
                filters {
                    excludes {
                        annotatedBy("androidx.compose.ui.tooling.preview.Preview*")
                        packages("dagger.hilt.internal.aggregatedroot.codegen", "hilt_aggregated_deps")
                        classes(
                            "*.BuildConfig",
                            "*Hilt_*",
                            "*_Factory*",
                            "*_GeneratedInjector",
                            "*_HiltModules*",
                            "*_MembersInjector",
                            "*ComposableSingletons*"
                        )
                    }
                }
            }
        }

        dependencies {
            add("ktlintRuleset", libs.library("compose-rules-ktlint"))
        }
    }
}
