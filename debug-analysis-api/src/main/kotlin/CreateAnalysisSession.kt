/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin

import com.intellij.mock.MockProject
import com.intellij.openapi.util.Disposer
import com.intellij.pom.PomModel
import com.intellij.pom.core.impl.PomModelImpl
import org.jetbrains.kotlin.analysis.api.standalone.StandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.utils.PathUtil
import kotlin.io.path.Path

const val KOTLIN_STDLIB_VERSION: String = "2.2.0"
const val KOTLIN_STDLIB_JAR: String = "kotlin-stdlib-$KOTLIN_STDLIB_VERSION.jar"

fun createFactory(): KtPsiFactory {
    val session = buildStandaloneAnalysisAPISession {
        println("creating session")
        buildKtModuleProvider {
            println("building module provider")
            platform = JvmPlatforms.defaultJvmPlatform
            addModule(buildKtSourceModule {
                println("adding module")
                moduleName = "Analysis module"
                platform = JvmPlatforms.defaultJvmPlatform
            })
            // Remove stdlib module for now to avoid null pointer issues
            // The Analysis API should work without explicit stdlib configuration
            println("done adding module")
        }
        println("done building module provider")
    }
    println("Done creating session")

    val project = session.project

    return KtPsiFactory(project)
}

@OptIn(K1Deprecation::class)
fun createFile(fileName: String, content: String, factory: KtPsiFactory): KtFile {
//    val disposable = Disposer.newDisposable()
//    val project: Project = KotlinCoreEnvironment.ProjectEnvironment(
//        disposable,
//        applicationEnvironment = KotlinCoreApplicationEnvironment.create(disposable, KotlinCoreApplicationEnvironmentMode.UnitTest),
//        configuration = CompilerConfiguration(),
//    ).project

//    val project = setupMyEnv(setupMyCfg()).project

    val psiFile = factory.createFile(fileName, content)
    return psiFile
}

fun setupMyCfg(): CompilerConfiguration {

    val cfg = CompilerConfiguration()

    val jdkRoots = PathUtil.getJdkClassesRootsFromCurrentJre()

    // TODO: Do not add the same jar file twice

    cfg.addJvmClasspathRoots(jdkRoots)

    cfg.put(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
    cfg.put(CommonConfigurationKeys.MODULE_NAME, JvmProtoBufUtil.DEFAULT_MODULE_NAME)

    return cfg
}

@OptIn(K1Deprecation::class)
fun setupMyEnv(cfg: CompilerConfiguration): KotlinCoreEnvironment {

    val disposable = Disposer.newDisposable()
    val env = KotlinCoreEnvironment.createForProduction(
        disposable,
        cfg,
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    )

//    val project = env.project as MockProject

//    val pomModel = PomModelImpl(env.project)
//    project.registerService(
//        PomModel::class.java,
//        pomModel
//    )

    (env.project as MockProject).registerService(KtPsiFactory::class.java, KtPsiFactory(env.project))

    return env
}
