pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

rootProject.name = "file-to-resource"

include(":example")
includeBuild("plugin-build")
