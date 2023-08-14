plugins {
    kotlin("jvm")
    application
    alias(libs.plugins.ktfmt)
}

dependencies {
    implementation(project(":protos"))

    implementation(libs.bundles.protobuf)
    implementation(libs.bundles.grpc)

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
