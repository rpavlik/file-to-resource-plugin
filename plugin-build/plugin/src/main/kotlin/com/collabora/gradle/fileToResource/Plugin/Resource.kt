package com.collabora.gradle.fileToResource.plugin

import org.gradle.api.Transformer
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class Resource constructor(public val name: String) {

    @Inject
    protected abstract fun getObjectFactory(): ObjectFactory

    val inputFile: RegularFileProperty = getObjectFactory().fileProperty()

    abstract val function: Property<Transformer<String, RegularFile>> // = getObjectFactory().property((Transformer<String, File>)::class.java)
}
