package com.collabora.gradle.fileToResource.plugin

import com.android.build.api.artifact.Artifact.SingleArtifact
import com.android.build.api.artifact.Artifact.Transformable
import com.android.build.api.artifact.ArtifactKind
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.crash.afterEvaluate
import com.google.common.collect.Lists
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import java.util.function.Consumer

@Suppress("UnstableApiUsage")
abstract class FileToResourcePlugin : Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "fileToResource"
        const val RAW_TASK_NAME_PREFIX = "fileToRawResource"
        const val TASK_NAME_PREFIX = "fileToResource"

        private fun registerResFolders(project: Project) {
            project.tasks.withType(TransformFileToRawResourceTask::class.java)
                .all { task: TransformFileToRawResourceTask ->
                    val dirs =
                        project.files(task.resourceDirectory).setBuiltBy(Lists.newArrayList(task))
                    onAllVariants(project) {
                        it.name
                        it.registerGeneratedResFolders(dirs)
                    }
                }
        }
        /**
         * Call the provided action on all variants of the Android application or library.
         *
         * This uses the pre-4.1.0 interface, which is the only way to get to registerGeneratedResFolders.
         */
        private fun onAllVariants(project: Project, action: (BaseVariant) -> Unit) {

            val androidAppExtension = project.extensions.findByType(AppExtension::class.java)
            if (androidAppExtension != null) {
                androidAppExtension.applicationVariants.all(action)
                return
            }
            val androidLibraryExtension = project.extensions.findByType(LibraryExtension::class.java)
            if (androidLibraryExtension != null) {
                androidLibraryExtension.libraryVariants.all(action)
                return
            }
        }
    }

    // Google apparently forgot to include something like this in ArtifactType,
    // at least in the 4.1.0 plugin. Could use ArtifactType.BUNDLE but it feels wrong
    object SingleFileTransformableArtifact :
        SingleArtifact<RegularFile>(ArtifactKind.FILE),
        Transformable

    override fun apply(project: Project) {
        // Add the 'fileToResource' extension object
        val extension =
            project.extensions.create(EXTENSION_NAME, FileToResourceExtension::class.java)

        registerTasks410(project, extension)

        // Only the Transform to raw resource tasks need to have their results added as resource dirs.
        // This syntax is awkward but needed for Kotlin 1.3.72 compat (because that's what gradle uses)
        afterEvaluate(
            Consumer<Project> { project1 ->
                registerResFolders(project1)
            }
        )
    }

    private fun registerTasks410(project: Project, extension: FileToResourceExtension) {
        val android = project.extensions.getByType(CommonExtension::class.java)
        android.onVariantProperties {
            extension.rawResources.all { resource ->
                // using this as org.gradle.api.Named here
                val taskProvider = project.tasks.register(
                    "$RAW_TASK_NAME_PREFIX${resource.name}${this.name.capitalize()}",
                    TransformFileToRawResourceTask::class.java
                ) { task ->
                    task.name.set(resource.name)
                    task.inputFile.set(resource.inputFile)
                    task.variantName.set(this.name)
                    task.function.set(resource.function)
                }
                // using this as ComponentProperties here
                this.artifacts.use(taskProvider).wiredWithFiles(
                    TransformFileToRawResourceTask::inputFile,
                    TransformFileToRawResourceTask::outputFile
                ).toTransform(SingleFileTransformableArtifact)
            }
        }

        // See https://github.com/android/gradle-recipes/blob/bd8336e32ae512c630911287ea29b45a6bacb73b/Kotlin/addCustomResValueFromTask/app/build.gradle.kts
        extension.stringResources.all { resource ->
            val taskProvider = project.tasks.register(
                "${TASK_NAME_PREFIX}${resource.name}",
                TransformFileTask::class.java
            ) { task ->
                task.name.set(resource.name)
                task.inputFile.set(resource.inputFile)
                task.function.set(resource.function)
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
    }
}
