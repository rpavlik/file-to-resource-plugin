package com.collabora.fileToResource.plugin

import com.collabora.filetoresource.plugin.FileToResourceExtension
import com.collabora.filetoresource.plugin.FileToResourcePlugin
import com.google.common.truth.Truth
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FileToResourcePluginTest {
    @Rule
    @JvmField
    val tmp: TemporaryFolder = TemporaryFolder()

    private lateinit var project: Project

    @Before
    fun prepare() {
        project = ProjectBuilder.builder()
            .withProjectDir(tmp.newFolder())
            .build()
        project.pluginManager.apply("com.android.application")
        project.pluginManager.apply(FileToResourcePlugin::class.java)
    }

    @Test
    fun `plugin is applied correctly to the project`() {
        Truth.assertThat(project.extensions.getByName(FileToResourcePlugin.EXTENSION_NAME)).isInstanceOf(
            FileToResourceExtension::class.java
        )
    }

    @Test
    fun `plugin does not create any tasks right away`() {
        project.tasks.names.onEach {
            Truth.assertThat(it).doesNotContain(FileToResourcePlugin.TASK_NAME_PREFIX)
            Truth.assertThat(it).doesNotContain(FileToResourcePlugin.RAW_TASK_NAME_PREFIX)
        }
    }
}
