plugins {
    kotlin("jvm")
    application
    alias(libs.plugins.ktfmt)
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.shared)

    testImplementation(libs.bundles.test)
}

tasks.test { 
    useJUnitPlatform() 
}

kotlin { 
    jvmToolchain(17) 
}

application {
    // Define the main class for the application.
    mainClass.set("server.AppMainKt")
    //enable development mode for enhanced error page reponses and hot reloading
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}
