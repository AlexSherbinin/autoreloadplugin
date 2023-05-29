package autoreload

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet
import kotlin.io.path.Path
import kotlin.io.path.exists


class AutoreloadPluginExtension {
    val watchSourceSets: List<SourceSet> = listOf()
}

class AutoreloadPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("autoreload", AutoreloadPluginExtension::class.java)
        if(project.gradle.startParameter.isContinuous) {
            project.tasks.withType(JavaExec::class.java) { task ->
                task.systemProperties["watchFiles"] = extension.watchSourceSets.asSequence()
                    .filter { !it.name.contains("test", ignoreCase = true) }
                    .map { it.allSource.sourceDirectories.asPath.split(";") }
                    .flatten()
                    .filter { Path(it).exists() }
                    .joinToString(";")
            }
        }
        project.dependencies.add("implementation", "com.github.AlexSherbinin.autoreloadplugin:dev:1.0-SNAPSHOT")
    }
}
