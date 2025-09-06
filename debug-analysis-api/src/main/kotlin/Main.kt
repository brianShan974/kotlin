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

    for (i in 1..500000) {
        println("Calling randprog_rs to generate Kotlin code...")
        val generatedCode = generator.generateKotlinCode()
//        val generatedCode = createFile("temp.kt", src, factory)

        println("Generated code:")
        println("=".repeat(50))
        println(generatedCode.text)
        println("=".repeat(50))

        // Now analyze the generated code
        println("\nAnalyzing the generated code...")
        try {
            val diagnostics = simpleAnalyze(generatedCode)

            println("\nAnalysis results:")
            println("=".repeat(50))
            if (diagnostics.isEmpty()) {
                println("No diagnostics found - code appears to be valid!")
            } else {
                println("Found ${diagnostics.size} diagnostics:")
                diagnostics.forEachIndexed { index, diagnostic ->
                    println("${index + 1}. $diagnostic")
                }
            }
            println("=".repeat(50))

        } catch (analysisError: Exception) {
            println("Error during analysis: ${analysisError.message}")
            println("Analysis failed but continuing with error information...")
            // Still record the error as a diagnostic result
            println("\nAnalysis results:")
            println("=".repeat(50))
            println("Found 1 diagnostics:")
            println("1. Analysis error: ${analysisError.javaClass.simpleName} - ${analysisError.message}")
            println("=".repeat(50))
        }
    }
}