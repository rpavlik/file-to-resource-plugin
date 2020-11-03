package com.collabora.gradle.fileToResource.plugin

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass", "UnstableApiUsage")
abstract class FileToResourceExtension @Inject constructor(private val objectFactory: ObjectFactory) {

    val stringResources: NamedDomainObjectCollection<Resource> = objectFactory.domainObjectContainer(
        Resource::class.java
    )

    val rawResources: NamedDomainObjectCollection<Resource> = objectFactory.domainObjectContainer(
        Resource::class.java
    )
}
