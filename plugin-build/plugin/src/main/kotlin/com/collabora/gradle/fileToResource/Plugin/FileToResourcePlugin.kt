package com.collabora.gradle.fileToResource.plugin

import com.android.build.api.artifact.Artifact.SingleArtifact
import com.android.build.api.artifact.Artifact.Transformable
import com.android.build.api.artifact.ArtifactKind
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile

@Suppress("UnstableApiUsage")
abstract class FileToResourcePlugin : Plugin<Project> {

    companion object {
        val EXTENSION_NAME = "fileToResource"
    }

    // Google apparently forgot to include something like this in ArtifactType,
    // at least in the 4.1.0 plugin. Could use ArtifactType.BUNDLE but it feels wrong - what's a "BUNDLE" anyway?
    object SINGLE_FILE_ARTIFACT : SingleArtifact<RegularFile>(ArtifactKind.FILE), Transformable

    override fun apply(project: Project) {
        // Add the 'fileToResource' extension object
        val extension = project.extensions.create(EXTENSION_NAME, FileToResourceExtension::class.java)

        val android = project.extensions.getByType(CommonExtension::class.java)
        android.onVariantProperties {
            val rawTaskProviders = extension.rawResources.map { resource ->
                val taskProvider = project.tasks.register("fileToRawResource${resource.name}${this.name.capitalize()}", GenerateRawResourceTask::class.java) { task ->
                    task.name.set(resource.name)
                    task.inputFile.set(resource.inputFile)
                    task.outputDirectory.set(project.layout.buildDirectory.map { it.dir("generated/fileToResource/res/raw/") })
                    task.outputFile.set(task.outputDirectory.map { it.file("${resource.name}.txt") })
                }
                // GenerateRawResourceTask.register(project, this, resource)
//                        project.tasks.register("fileToRawResource${resource.name}${this.name}", GenerateRawResourceTask::class.java) {
//                    it.name.set(resource.name)
//                    it.function.set(resource.function)
//                    it.inputFile.set(resource.inputFile)
//
//                }
                artifacts.use(taskProvider).wiredWithFiles(
                    GenerateRawResourceTask::inputFile,
                    GenerateRawResourceTask::outputFile
                ).toTransform(SINGLE_FILE_ARTIFACT)
                taskProvider
            }
        }

        // See https://github.com/android/gradle-recipes/blob/bd8336e32ae512c630911287ea29b45a6bacb73b/Kotlin/addCustomResValueFromTask/app/build.gradle.kts
        extension.stringResources.all { resource ->
            val taskProvider = project.tasks.register("fileToResource${resource.name}", GenerateRawResourceTask::class.java) { task ->
                task.name.set(resource.name)
                task.inputFile.set(resource.inputFile)
                task.outputDirectory.set(project.layout.buildDirectory.map { it.dir("intermediates/fileToResource/") })
                task.outputFile.set(task.outputDirectory.map { it.file("${resource.name}.txt") })
            }
            android.onVariantProperties {
                addResValue(
                    resource.name,
                    "string",
                    taskProvider.map { task -> task.outputFile.get().asFile.readText(Charsets.UTF_8) },
                    "Computed from ${resource.inputFile}"
                )
            }
        }

        // Add a task that uses configuration from the extension object
//        project.tasks.register(TASK_NAME, FileToResourceTask::class.java) {
//            it.inputFile.set(extension.tag)
//            it.message.set(extension.message)
//            it.outputFile.set(extension.outputFile)
//        }
//        project.android.applicationVariants.all {
//            variant ->
//            createTasks(project, variant)
//        }
    }
}
