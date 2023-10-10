import java.nio.file.Files
import org.jetbrains.kotlin.incremental.createDirectory

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    alias(libs.plugins.ktfmt)
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.shared)
    implementation(libs.bundles.persistence)
    implementation(libs.quartz)

    testImplementation(libs.bundles.test)
}

tasks.test { 
    useJUnitPlatform()
    //build testing root tmp directory
    val testingTempDir = project.layout.buildDirectory.dir("tmp")

    //clear the testing directory
    testingTempDir.get().asFile.let {
        if (it.exists()) {
            it.delete()
        }
        it.createDirectory()
    }
    systemProperty("application.sqlite.data_path", ":memory:")
    //only 1 connection for tests to ensure the same in memory db is connected to
    systemProperty("application.sqlite.num_connections", "1")
    systemProperty("application.testing.temp_dir", testingTempDir.get().toString())
}

kotlin { 
    jvmToolchain(17) 
}

application {
    // Define the main class for the application.
    mainClass.set("server.AppMainKt")
    //enable development mode for enhanced error page responses and hot reloading
    applicationDefaultJvmArgs = listOf(
        "-Dio.ktor.development=true",
        "-Dapplication.sqlite.data_path=/tmp/com.unclone/sqlite/data.db"
    )
    sourceSets {
        main {
            resources.srcDir("/src/main/resources/dev")
        }
    }
}
