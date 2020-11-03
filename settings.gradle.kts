pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

rootProject.name = ("file-to-resource-plugin")

// include(":example")
includeBuild("plugin-build")
