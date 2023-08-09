plugins {
    kotlin("jvm") version "1.9.0" 
    application
    alias(libs.plugins.ktfmt)
}

dependencies {
    implementation(project(":protos"))

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
    mainClass.set("server.ServerKt")
}
