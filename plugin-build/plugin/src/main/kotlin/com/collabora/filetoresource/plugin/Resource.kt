package com.collabora.filetoresource.plugin

import org.gradle.api.Transformer
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

@Suppress("UnnecessaryAbstractClass")
abstract class Resource constructor(val name: String) {

    abstract val inputFile: RegularFileProperty

    abstract val function: Property<Transformer<String, RegularFile>>
}
