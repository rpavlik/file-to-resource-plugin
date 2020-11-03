package com.collabora.gradle.fileToResource.plugin

import com.google.common.truth.Truth.assertThat
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

    lateinit var project: Project

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
        assertThat(project.extensions.getByName(FileToResourcePlugin.EXTENSION_NAME)).isInstanceOf(FileToResourceExtension::class.java)
    }
}
