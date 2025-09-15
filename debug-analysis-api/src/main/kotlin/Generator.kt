package org.jetbrains.kotlin

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit

/**
 * Generator class that calls a Rust program to generate random Kotlin code
 */
class Generator(val factory: KtPsiFactory) {

    /**
     * Path to the extracted Rust executable
     */
    private val rustProgramPath: String

    init {
        // Extract the Rust executable from JAR resources to a temporary file
        rustProgramPath = extractRustExecutable()
    }

    /**
     * Extracts the randprog_rs executable from JAR resources to a temporary file
     * @return Path to the extracted executable
     */
    private fun extractRustExecutable(): String {
        val resourceStream = this::class.java.classLoader.getResourceAsStream("libs/rprs")
            ?: throw RuntimeException("Cannot find randprog_rs in JAR resources")
        
        // Create a temporary file
        val tempFile = File.createTempFile("randprog_rs", "")
        tempFile.deleteOnExit()
        
        // Copy the resource to the temporary file
        Files.copy(resourceStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        
        // Make the file executable
        tempFile.setExecutable(true)
        
        return tempFile.absolutePath
    }

    val processBuilder = ProcessBuilder(rustProgramPath)

    init {
        processBuilder.redirectErrorStream(true) // Redirect error stream to output stream
    }

    /**
     * Generates random Kotlin code by calling the Rust program
     * @return Generated Kotlin source code as a string
     */
    fun generateKotlinCode(): KtFile {

        val process = processBuilder.start()

        // Read output from the process
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }

        // Wait for the process to complete with a timeout
        val exited = process.waitFor(30, TimeUnit.SECONDS) // 30-second timeout

        if (!exited) {
            process.destroyForcibly()
            throw RuntimeException("Rust program timed out after 30 seconds.")
        }

        val exitCode = process.exitValue()
        if (exitCode != 0) {
            val errorOutput = output.toString() // Error stream was redirected to output
            throw RuntimeException("Rust program exited with error code $exitCode. Output:\n$errorOutput")
        }

        val src = output.toString().trim()
        return createFile("temp.kt", src, factory)
    }
}

