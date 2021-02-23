package com.github.jschneidereit.ketch.plugin

import com.google.cloud.tools.jib.gradle.JibPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Classpath
import javax.inject.Inject

private const val ketch = "ketch"

class KetchBasePlugin : Plugin<Project> {
    private lateinit var kotestExtension: KotestExtension
    private lateinit var jibExtension: JibExtension

    private lateinit var ketchConfiguration: Configuration

    override fun apply(project: Project) {
        kotestExtension = project.extensions.create("kotest", KotestExtension::class.java)
        jibExtension = project.extensions.create("jib", JibExtension::class.java)

        ketchConfiguration = project.configurations.create(ketch)
        ketchConfiguration.defaultDependencies {
            val kotestVersion = kotestExtension.version.get()
            // TODO: add dependency to ketch library
            it.add(project.dependencies.create("io.kotest:kotest-bom:$kotestVersion"))
            it.add(project.dependencies.create("io.kotest:kotest-framework-engine-jvm:$kotestVersion"))
        }

        project.pluginManager.apply("application")
        project.pluginManager.apply(JibPlugin::class.java)
    }
}

class KetchPlugin : Plugin<Project> {
    private lateinit var project: Project
    private lateinit var ketchBasePlugin: KetchBasePlugin

    override fun apply(project: Project) {
        this.project = project

        ketchBasePlugin = project.plugins.apply(KetchBasePlugin::class.java)
    }
}

class JibExtension @Inject constructor(factory: ObjectFactory) {
    val version = factory.property(String::class.java).convention("2.7.1")
    val config = factory.mapProperty(String::class.java, String::class.java)

}

class KotestExtension @Inject constructor(factory: ObjectFactory) {
    val version = factory.property(String::class.java).convention("4.4.1")
    val config = factory.mapProperty(String::class.java, String::class.java)
}


interface KetchTask : Task {
    @get:Classpath val lombokClasspath: ConfigurableFileCollection?
}

class GenerateJibConfig : DefaultTask() {

}