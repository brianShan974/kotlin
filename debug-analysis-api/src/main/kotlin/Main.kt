package org.jetbrains.kotlin

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.standalone.StandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import kotlin.io.path.Path

fun main(fileNames: Array<String>) {
    val classLoader = Thread.currentThread().contextClassLoader

    fileNames.map {
        val session = createSession()
        val src = classLoader.getResource(it)!!.readText()
        val psiFile = createFile(it, src.trimIndent(), session)
        try {
            val diagnostics = simpleAnalyze(session, psiFile)
//        println("diagnostics: $diagnostics")
            println("done analyzing, there are ${diagnostics.size} diagnostics in total")
            for (diagnostic in diagnostics) {
                println(diagnostic)
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            println("Now we're back")
        }
    }
}

fun simpleAnalyze(session: StandaloneAnalysisAPISession, file: KtFile): List<String> {
    println("entering simple analyze")

//    @OptIn(KaExperimentalApi::class)
//    psiFile.contextModule = KaModuleProvider.getModule(session.project, psiFile, useSiteModule = null)
//    println("Done creating context module")

    val diagnostics = analyze(file) {
        println("inside analyze")
        val diagnostics = file.collectDiagnostics(KaDiagnosticCheckerFilter.EXTENDED_AND_COMMON_CHECKERS)
        println("done analyzing")
        diagnostics.map {
            it.defaultMessage
        }
    }

    println("finished simple analyze")
    return diagnostics
}

fun createSession(): StandaloneAnalysisAPISession {
    val session = buildStandaloneAnalysisAPISession {
        println("creating session")
        buildKtModuleProvider {
            println("building module provider")
            platform = JvmPlatforms.defaultJvmPlatform
            addModule(buildKtSourceModule {
                println("adding module")
                moduleName = "Analysis module"
                platform = JvmPlatforms.defaultJvmPlatform
                addSourceRoots(listOf(Path("")))
            })
            println("done adding module")
        }
        println("done building module provider")
    }
    println("Done creating session")

    return session
}

fun createFile(fileName: String, content: String, session: StandaloneAnalysisAPISession): KtFile {
    val factory = KtPsiFactory(session.project)
    val psiFile = factory.createFile(fileName, content)
    return psiFile
}