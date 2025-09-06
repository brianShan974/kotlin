/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.psi.KtFile

fun simpleAnalyze(file: KtFile): List<String> {
    ColorPrint.teal("entering simple analyze")

    return try {
        val diagnostics = analyze(file) {
            ColorPrint.magenta("inside analyze")
            try {
                // Try with only common checkers first (most stable)
                val diagnostics = file.collectDiagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)
                ColorPrint.lime("done analyzing with common checkers")
                diagnostics.map {
                    it.defaultMessage
                }
            } catch (e: Throwable) {
                ColorPrint.brightRed("Error with common checkers: ${e.message}")
                ColorPrint.orange("Error type: ${e.javaClass.simpleName}")
                // Return the error as a diagnostic
                listOf("Analysis error: ${e.javaClass.simpleName} - ${e.message}")
            }
        }
        ColorPrint.silver("finished simple analyze")
        diagnostics
    } catch (e: Throwable) {
        ColorPrint.coral("Error during analysis: ${e.message}")
        ColorPrint.pink("Error type: ${e.javaClass.simpleName}")
        // Return error information instead of crashing
        listOf("Analysis error: ${e.javaClass.simpleName} - ${e.message}")
    }
}
