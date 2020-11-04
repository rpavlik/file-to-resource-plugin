package com.collabora.gradle.fileToResource.plugin

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

@Suppress("UnstableApiUsage")
abstract class TransformFileToRawResourceTask : TransformFileTask() {

    @get:Input
    abstract val variantName: Property<String>

    val resourceDirectory: DirectoryProperty by lazy {
        project.objects.directoryProperty().value(
            project.layout.buildDirectory
                .dir("generated/fileToResource")
                .flatMap { it.dir(variantName) }
                .map { it.dir("res") }
        )
    }

    override val outputDirectory: DirectoryProperty by lazy {
        project.objects.directoryProperty().value(
            resourceDirectory.dir("raw")
        )
    }
}
