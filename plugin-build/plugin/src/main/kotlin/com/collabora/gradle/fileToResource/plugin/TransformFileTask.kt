@file:Suppress("UnstableApiUsage")

package com.collabora.gradle.fileToResource.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.Transformer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class TransformFileTask : DefaultTask() {

    init {
        description = "Transform a file, for use as a raw resources or for later, other usage"

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

    @get:Option(option = "outputDirectory", description = "The place to put the output")
    abstract val outputDirectory: DirectoryProperty

    private val outFileName: Provider<String> by lazy {
        name.map { "$it.txt" }
    }

    @get:OutputFile
    val outputFile: RegularFileProperty by lazy {
        project.objects.fileProperty().value(outputDirectory.file(outFileName))
    }

    @TaskAction
    fun produce() {
        // Set a default output directory
        outputDirectory.convention(project.layout.buildDirectory.dir("generated/TransformFile"))
        val result = function.get().transform(inputFile.get())
        outputFile.get().asFile.also {
            it.parentFile.mkdirs()
            it.writeText(result)
        }
        logger.info("Wrote contents for resource $name")

    }

}
