package org.jetbrains.kotlin

const val src = """
fun answer(): Int {
    return 42
}

fun another() {
    answer(42)
}
"""

fun main(fileNames: Array<String>) {
    val factory = createFactory()
    val generator = Generator(factory)

    for (i in 1..1) {
        ColorPrint.brightCyan("Calling randprog_rs to generate Kotlin code...")
        val generatedCode = generator.generateKotlinCode()
//        val generatedCode = createFile("temp.kt", src, factory)

        ColorPrint.blue("Generated code:")
        ColorPrint.gold("=".repeat(50))
        ColorPrint.lime(generatedCode.text)
        ColorPrint.gold("=".repeat(50))

        // Now analyze the generated code
        ColorPrint.purple("\nAnalyzing the generated code...")
        try {
            val diagnostics = simpleAnalyze(generatedCode)

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

        } catch (analysisError: Exception) {
            ColorPrint.brightRed("Error during analysis: ${analysisError.message}")
            ColorPrint.magenta("Analysis failed but continuing with error information...")
            // Still record the error as a diagnostic result
            ColorPrint.brightGreen("\nAnalysis results:")
            ColorPrint.silver("=".repeat(50))
            ColorPrint.pink("Found 1 diagnostics:")
            ColorPrint.brightRed("1. Analysis error: ${analysisError.javaClass.simpleName} - ${analysisError.message}")
            ColorPrint.silver("=".repeat(50))
        }
    }
    
    ColorPrint.brightGreen("Program completed successfully.")
    ColorPrint.brightCyan("Exiting program...")
    System.exit(0) // Force exit to ensure program terminates
}