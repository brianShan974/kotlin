plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    jacoco
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
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(true)
    }

    classDirectories.setFrom(
        files(
            fileTree(project.rootDir.resolve("analysis/analysis-api-fir/build/classes/kotlin/main")) {
                include("**/*")
            },
            fileTree(project.rootDir.resolve("analysis/low-level-api-fir/build/classes/kotlin/main")) {
                include("**/*")
            },
            fileTree(project.rootDir.resolve("analysis/analysis-api-impl-base/build/classes/kotlin/main")) {
                include("**/*")
            },
            fileTree(project.rootDir.resolve("analysis/analysis-api-platform-interface/build/classes/kotlin/main")) {
                include("**/*")
            },
            fileTree(project.rootDir.resolve("compiler/fir/checkers/build/classes/kotlin/main")) {
                include("**/*")
            },
            fileTree(project.rootDir.resolve("compiler/fir/checkers/checkers.js/build/classes/kotlin/main")) {
                include("**/*")
            },
            fileTree(project.rootDir.resolve("compiler/fir/checkers/checkers.web.common/build/classes/kotlin/main")) {
                include("**/*")
            },
        ),
    )
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.50".toBigDecimal()
            }
        }
    }
}

application {
    mainClass.set("org.jetbrains.kotlin.MainKt")
}

tasks.jar {
    archiveBaseName.set("debug-analysis-api")
    archiveVersion.set("1.0.0")

    manifest {
        attributes(
            "Main-Class" to "org.jetbrains.kotlin.MainKt",
            "Implementation-Title" to "Kotlin Analysis API Debug Tool",
            "Implementation-Version" to archiveVersion.get(),
            "Implementation-Vendor" to "JetBrains",
        )
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Creates a fat JAR with all dependencies and resources"

    archiveBaseName.set("debug-analysis-api")
    archiveVersion.set("1.0.0")
    archiveClassifier.set("all")

    manifest {
        attributes(
            "Main-Class" to "org.jetbrains.kotlin.MainKt",
            "Implementation-Title" to "Kotlin Analysis API Debug Tool",
            "Implementation-Version" to archiveVersion.get(),
            "Implementation-Vendor" to "JetBrains",
        )
    }

    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath
            .get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    from(sourceSets.main.get().resources) {
        include("**/*")
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

