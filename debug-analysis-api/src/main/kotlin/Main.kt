package org.jetbrains.kotlin

import java.io.File
import java.nio.charset.StandardCharsets

const val src = """
fun answer(): Int {
    return 42
}

fun another() {
    answer(42)
}
"""

fun main(fileNames: Array<String>) {
    val args = fileNames.toList()
    val scriptFlag = args.contains("--script")
    val showSerializable = args.contains("--show-serializable")

    ColorPrint.enableColors = !scriptFlag

    val actualArgs = args.filter { it != "--script" && it != "--show-serializable" }

    val factory = createFactory(scriptFlag)
    val generator = Generator(factory)

    val fuzzingMode = actualArgs.isEmpty()

    val total = if (fuzzingMode) {
        10000
    } else {
        1
    }

    for (i in 1..total) {
        if (!scriptFlag) {
            ColorPrint.brightCyan("Calling randprog_rs to generate Kotlin code...")
        }
        val generatedCode = if (fuzzingMode) {
            generator.generateKotlinCode()
        } else {
            createFile("temp.kt", File(actualArgs[0]).readText(StandardCharsets.UTF_8), factory)
        }
//        val generatedCode = createFile("temp.kt", src, factory)

        if (!scriptFlag) {
            ColorPrint.blue("Generated code:")
            ColorPrint.gold("=".repeat(50))
            ColorPrint.lime(generatedCode.text)
            ColorPrint.gold("=".repeat(50))

            // Now analyze the generated code
            ColorPrint.purple("\nAnalyzing the generated code...")
        }
        try {
            val diagnostics = simpleAnalyze(generatedCode, scriptFlag, showSerializable)

            if (!scriptFlag) {
                ColorPrint.green("\nAnalysis results:")
                ColorPrint.teal("=".repeat(50))
                if (diagnostics.isEmpty()) {
                    ColorPrint.brightGreen("No diagnostics found - code appears to be valid!")
                } else {
                    ColorPrint.orange("Found ${diagnostics.size} diagnostics:")
                    diagnostics.forEachIndexed { index, diagnostic ->
                        ColorPrint.coral("${index + 1}. $diagnostic")
                    }
                }
                ColorPrint.teal("=".repeat(50))
            } else {
                if (diagnostics.isEmpty()) {
                } else {
                    diagnostics.forEach { diagnostic ->
                        println(diagnostic)
                    }
                }
            }

        } catch (analysisError: Exception) {
            if (!scriptFlag) {
                ColorPrint.brightRed("Error during analysis: ${analysisError.message}")
                ColorPrint.magenta("Analysis failed but continuing with error information...")
                // Still record the error as a diagnostic result
                ColorPrint.brightGreen("\nAnalysis results:")
                ColorPrint.silver("=".repeat(50))
                ColorPrint.pink("Found 1 diagnostics:")
                ColorPrint.brightRed("1. Analysis error: ${analysisError.javaClass.simpleName} - ${analysisError.message}")
                ColorPrint.silver("=".repeat(50))
            } else {
                println("Analysis error: ${analysisError.javaClass.simpleName} - ${analysisError.message}")
            }
        }
    }

    if (!scriptFlag) {
        ColorPrint.brightGreen("Program completed successfully.")
        ColorPrint.brightCyan("Exiting program...")
    }
    System.exit(0) // Force exit to ensure program terminates
}
