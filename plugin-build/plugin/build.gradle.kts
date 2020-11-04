plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
    `maven-publish`
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect", BuildPluginsVersion.KOTLIN))
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:4.1.0")

    testImplementation(TestingLib.JUNIT)

    testImplementation("com.google.truth:truth:1.1")
    testImplementation("com.google.truth.extensions:truth-java8-extension:1.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "com.collabora.filetoresource.plugin"
version = "0.9.1"

val pluginId = "fileToResourcePlugin"

gradlePlugin {
    plugins {
        create(pluginId) {
            id = "${project.group}"
            implementationClass = "${project.group}.FileToResourcePlugin"
        }
    }
}

// Configuration Block for the Plugin Marker artifact on Plugin Central
pluginBundle {
    website = "https://github.com/rpavlik/file-to-resource-plugin"
    vcsUrl = "https://github.com/rpavlik/file-to-resource-plugin"
    description = "Process a file somehow to produce an Android resource"
    tags = listOf(
        "plugin",
        "gradle",
        "android",
        "resource"
    )

    plugins {
        getByName(pluginId) {
            displayName = "File-to-Resource Gradle Plugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
        maven {
            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri("$buildDir/repos/releases")
            val snapshotsRepoUrl = uri("$buildDir/repos/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}
