package org.jetbrains.kotlin

fun main(fileNames: Array<String>) {
    val classLoader = Thread.currentThread().contextClassLoader

    fileNames.map {
//        val session = createSession()
        val src = classLoader.getResource("src_files/$it")!!.readText()
        println("source code: \n$src")
        val psiFile = createFile(it, src.trimIndent()/*, session*/)
        try {
            val diagnostics = simpleAnalyze(psiFile)
//            println("diagnostics: $diagnostics")
            println("\nThe diagnostics are:")
            for (diagnostic in diagnostics) {
                println(diagnostic)
            }
            println("done analyzing, there are ${diagnostics.size} diagnostics in total")
        } catch (e: Exception) {
            println("Error: ${e.message}")
            println("Now we're back")
        }
    }
}
