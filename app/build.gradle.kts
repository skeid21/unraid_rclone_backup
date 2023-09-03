plugins {
    kotlin("jvm")
    application
    alias(libs.plugins.ktfmt)
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.shared)
    implementation(libs.bundles.persistence)
    implementation(libs.quartz)

    testImplementation(libs.bundles.test)
}

tasks.test { 
    useJUnitPlatform()
    val buildDBPath = layout.buildDirectory.file("tmp/com.unclone/sqlite/data.db").get().asFile.path
    jvmArgs = listOf(
        "-Dapplication.sqlite.data_path=$buildDBPath"
    )
}

kotlin { 
    jvmToolchain(17) 
}

application {
    // Define the main class for the application.
    mainClass.set("server.AppMainKt")
    //enable development mode for enhanced error page reponses and hot reloading
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
