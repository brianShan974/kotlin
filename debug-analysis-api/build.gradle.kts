plugins {
    kotlin("jvm")
    application
}

group = "org.jetbrains.kotlin"
version = "2.3.255-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":analysis"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.jetbrains.kotlin.MainKt")
}