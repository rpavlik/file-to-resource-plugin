package com.collabora.gradle.fileToResource.plugin

import com.android.build.api.variant.VariantProperties
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option

abstract class GenerateRawResourceTask : DefaultTask() {

    init {
        description = "Convert a file into an Android raw resource"

        // Don't forget to set the group here.
        group = BasePlugin.BUILD_GROUP
    }

    @get:Input
    @get:Option(option = "function", description = "The user-supplied function")
    abstract val function: Property<Transformer<String, RegularFile>>

    @get:Input
    @get:Option(option = "name", description = "The name of resource")
    abstract val name: Property<String>

    @get:InputFile
    @get:Option(option = "input", description = "The input file to process")
    abstract val inputFile: RegularFileProperty

    //    @get:Output
    @Internal
    val result: Provider<String> = inputFile.map { function.get().transform(it) }

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty // Provider<Directory> = project.layout.buildDirectory.map { it.dir("generated/fileToResource/res/raw/") }

//    @get:OutputFile
//    val outputFile: Provider<RegularFile> = outputDirectory.map { it.file("${name}.txt") }

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun produce() {
        val outFile = outputFile.get().asFile
        outFile.writeText(result.get())
        logger.info("Wrote contents for resource $name")
    }

    companion object {
        fun register(project: Project, variantProperties: VariantProperties, resource: Resource): TaskProvider<GenerateRawResourceTask> {
            return project.tasks.register("fileToRawResource${resource.name}${variantProperties.name.capitalize()}", GenerateRawResourceTask::class.java) { task ->
                task.name.set(resource.name)
                task.inputFile.set(resource.inputFile)
                task.outputDirectory.set(project.layout.buildDirectory.map { it.dir("generated/fileToResource/res/raw/") })
                task.outputFile.set(task.outputDirectory.map { it.file("${resource.name}.txt") })
            }
        }
    }
}
