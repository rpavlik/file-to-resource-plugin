object PluginCoordinates {
    const val ID = "com.collabora.gradle.fileToResource.plugin"
    const val GROUP = "com.collabora.gradle.fileToResource"
    const val VERSION = "1.0.0"
    const val IMPLEMENTATION_CLASS = "com.collabora.gradle.fileToResource.plugin.FileToResourcePlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/rpavlik/file-to-resource-plugin"
    const val WEBSITE = "https://github.com/rpavlik/file-to-resource-plugin"
    const val DESCRIPTION = "Process a file somehow to produce an Android resource"
    const val DISPLAY_NAME = "File-to-Android-resource process"
    val TAGS = listOf(
        "plugin",
        "gradle",
        "android",
        "resource"
    )
}

