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

    val diagnostics = analyze(file) {
        println("inside analyze")
        try {
            val diagnostics = file.collectDiagnostics(KaDiagnosticCheckerFilter.EXTENDED_AND_COMMON_CHECKERS)
            println("done analyzing with common checkers")
            diagnostics.map {
                it.defaultMessage
            }
        } catch (e: Throwable) {
            println("Error with common checkers: ${e.message}")
            listOf("Analysis error: ${e.javaClass.simpleName} - ${e.message}")
        }
    }
    println("finished simple analyze")
    return diagnostics
}
