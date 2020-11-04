package com.collabora.filetoresource.plugin

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass", "UnstableApiUsage")
abstract class FileToResourceExtension @Inject constructor(objectFactory: ObjectFactory) {

    val stringResources: NamedDomainObjectCollection<Resource> =
        objectFactory.domainObjectContainer(
            Resource::class.java
        )

    val rawResources: NamedDomainObjectCollection<Resource> = objectFactory.domainObjectContainer(
        Resource::class.java
    )
}
