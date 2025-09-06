package org.jetbrains.kotlin

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Generator class that calls a Rust program to generate random Kotlin code
 */
class Generator(val factory: KtPsiFactory) {

    /**
     * Path to the Rust executable that generates Kotlin code
     */
    private val rustProgramPath: String = this::class.java.classLoader.getResource("libs/randprog_rs")!!.path
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

