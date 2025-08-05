plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "org.jetbrains.kotlin"
version = "2.3.255-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("it.unimi.dsi:fastutil:8.5.16")
    implementation("org.codehaus.woodstox:stax2-api:4.2.2")
    implementation("com.fasterxml.woodstox:woodstox-core:7.1.1")
    implementation("com.fasterxml:aalto-xml:1.3.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")

    implementation(kotlin("stdlib"))

    implementation(project(":analysis:analysis-api"))
    implementation(project(":analysis:analysis-api-standalone"))
    implementation(project(":compiler:psi:psi-api"))
    implementation(project(":compiler:cli-common"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.jetbrains.kotlin.MainKt")
}