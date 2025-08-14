/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.psi.KtFile

fun simpleAnalyze(file: KtFile): List<String> {
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
