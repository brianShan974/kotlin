package org.jetbrains.kotlin

import com.intellij.mock.MockProject
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.diagnostics.KaDiagnosticWithPsi
import org.jetbrains.kotlin.analysis.api.projectStructure.contextModule
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.collectDiagnosticsForFile
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtPsiFactory
import kotlin.io.path.Path

const val src1 = """
enum class ExecutionState {
    Running,
    Finished,
}

interface Error {
    val message: String
}

enum class ExecutionError : Error {
    ProgramCounterOutOfRange {
        override val message = "Index Error: The program counter is out of range."
    },
    TooFarLeft {
        override val message = "Index Error: You have gone too far to the left!"
    },
    TooFarRight {
        override val message = "Index Error: You have gone too far to the right!"
    },
    Overflow {
        override val message = "Overflow Error: The number in the cell has reached its maximum!"
    },
    Underflow {
        override val message = "Overflow Error: The number in the cell has reached its minimum!"
    },
    InvalidCharacter {
        override val message = "Value Error: The value in the cell is not a valid Unicode character!"
    },
    InputError {
        override val message = "IO Error: Unable to get character input!"
    }
}

class RuntimeError(val err: ExecutionError) : Throwable(err.message)

sealed class ExecutorCommand {
    object MoveRight : ExecutorCommand()
    object MoveLeft : ExecutorCommand()
    object Increment : ExecutorCommand()
    object Decrement : ExecutorCommand()
    object Output : ExecutorCommand()
    class JumpForward(val pos: Int) : ExecutorCommand()
    class JumpBack(val pos: Int) : ExecutorCommand()
}

typealias ExecutionResult = Result<ExecutionState>

class ExecutorState(val commands: List<ExecutorCommand>) {
    var ptr: Int = 0
    var cells = MutableList<Int>(32, init = { 0 })
    var pc: Int = 0

    fun executeOnce(): ExecutionResult {
        if (this.pc >= this.commands.size) {
            return Result.success(ExecutionState.Finished)
        }

        val currentCommand = this.commands[this.pc]

        val executionResult = this.executeCommand(currentCommand)
        if (executionResult.isFailure) {
            return executionResult
        }
        this.incrementPc()

        return Result.success(ExecutionState.Running)
    }

    fun executeCommand(currentCommand: ExecutorCommand): ExecutionResult {
        val error = this.checkStateValid()
        if (error != null) {
            return Result.failure(RuntimeError(error))
        }

        return when (currentCommand) {
            is ExecutorCommand.MoveRight -> this.moveToTheRight()
            is ExecutorCommand.MoveLeft -> this.moveToTheLeft()
            is ExecutorCommand.Increment -> this.increment()
            is ExecutorCommand.Decrement -> this.decrement()
            is ExecutorCommand.Output -> this.output()
            is ExecutorCommand.JumpForward -> this.jumpForward(currentCommand.pos)
            is ExecutorCommand.JumpBack -> this.jumpBack(currentCommand.pos)
        }
    }

    fun incrementPc() {
        this.pc++
    }

    fun checkStateValid(): ExecutionError? {
        return if (this.pc >= this.commands.size || this.pc < 0) {
            ExecutionError.ProgramCounterOutOfRange
        } else if (this.ptr >= this.cells.size) {
            ExecutionError.TooFarRight
        } else if (this.ptr < 0) {
            ExecutionError.TooFarLeft
        } else {
            null
        }
    }

    fun moveToTheRight(): ExecutionResult {
        this.ptr++

        if (this.ptr >= this.cells.size) {
            this.cells.addLast(0)
        }

        return Result.success(ExecutionState.Running)
    }

    fun moveToTheLeft(): ExecutionResult {
        return if (this.ptr == 0) {
            Result.failure(RuntimeError(ExecutionError.TooFarLeft))
        } else {
            this.ptr--
            Result.success(ExecutionState.Running)
        }
    }

    fun increment(): ExecutionResult {
        return if (this.cells[this.ptr] == Int.MAX_VALUE) {
            Result.failure(RuntimeError(ExecutionError.Overflow))
        } else {
            this.cells[this.ptr]++
            Result.success(ExecutionState.Running)
        }
    }

    fun decrement(): ExecutionResult {
        return if (this.cells[this.ptr] == Int.MIN_VALUE) {
            Result.failure(RuntimeError(ExecutionError.Underflow))
        } else {
            this.cells[this.ptr]--
            Result.success(ExecutionState.Running)
        }
    }

    fun output(): ExecutionResult {
        val char = this.cells[this.ptr].toChar()

        return if (char.isDefined()) {
            print(char)
            Result.success(ExecutionState.Running)
        } else {
            Result.failure(RuntimeError(ExecutionError.InvalidCharacter))
        }
    }

    fun jumpForward(pos: Int): ExecutionResult {
        return if (pos > this.commands.size) {
            Result.failure(RuntimeError(ExecutionError.TooFarRight))
        } else if (pos < 0) {
            Result.failure(RuntimeError(ExecutionError.TooFarLeft))
        } else {
            if (this.cells[this.ptr] == 0) {
                this.pc = pos
            }
            Result.success(ExecutionState.Running)
        }
    }

    fun jumpBack(pos: Int): ExecutionResult {
        if (this.cells[this.ptr] != 0) {
            this.pc = pos
        }

        return Result.success(ExecutionState.Running)
    }
}

fun parse(src: String): Result<List<ExecutorCommand>> {
    val commands = mutableListOf<ExecutorCommand>()
    val posInCommands = ArrayDeque<Int>()

    var currentCmdPtr = 0

    for (char in src) {
        val currentCmd = when (char) {
            '>' -> ExecutorCommand.MoveRight
            '<' -> ExecutorCommand.MoveLeft
            '+' -> ExecutorCommand.Increment
            '-' -> ExecutorCommand.Decrement
            '.' -> ExecutorCommand.Output
            '[' -> {
                posInCommands.addLast(currentCmdPtr)
                ExecutorCommand.JumpForward(0)
            }
            ']' -> {
                val lastPos = posInCommands.removeLastOrNull()
                if (lastPos == null) {
                    return Result.failure(Throwable("Syntax Error: '['s and ']'s do not properly match. There are more ']'s than '['s"))
                }
                commands[lastPos] = ExecutorCommand.JumpForward(currentCmdPtr)
                ExecutorCommand.JumpBack(lastPos)
            }

            else -> continue
        }

        currentCmdPtr++
        commands.addLast(currentCmd)
    }

    return if (posInCommands.isNotEmpty()) {
        Result.failure(Throwable("Syntax Error: '['s and ']'s do not properly match. There are more '['s than ']'s"))
    } else {
        Result.success(commands)
    }
}

fun main(args: Array<String>) {
    val src = \"\"\"
++++[>+++++<-]>[<+++++>-]+<+[
    >[>+>+<<-]++>>[<<+>>-]>>>[-]++>[-]+
    >>>+[[-]++++++>>>]<<<[[<++++++++<++>>-]+<.<[>----<-]<]
    <<[>>>>>[>>>[-]+++++++++<[>-<-]+++++++++>[-[<->-]+[<<<]]<[>+<-]>]<<-]<<-
]
    \"\"\"

    val commands = parse(src)

    val executor = ExecutorState(commands.getOrThrow())

    do {
        val result = executor.executeOnce()
    } while (result.isSuccess and (result.getOrThrow() == ExecutionState.Running))
}
"""

const val src2 = """
fun answer(): Int {
    return 42
}

fun main() {
    answer(42, 42)
    println("Hello, world!")
}
"""

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        print(src2)
        simpleAnalyze(src2.trimIndent())
        return
    }
//    val parser = ArgParser("brianFuck974-kt")
//
//    val mode by parser.option(
//        ArgType.String,
//        fullName = "type",
//        shortName = "t",
//        description = "The type of the input, either f for file path or s for source code"
//    )
//
//    val content by parser.argument(
//        ArgType.String,
//    )
//
//    parser.parse(args)
//
//    val src = try {
//        when (mode) {
//            "s" -> content
//            else -> {
//                println("Invalid mode. Please use -t f or -t s")
//                return
//            }
//        }
//    } catch (e: Exception) {
//        println("Error: ${e.message}")
//        return
//    }

    println(src2)

    simpleAnalyze(src2.trimIndent())
}

fun simpleAnalyze(fileContent: String) {
    val session = buildStandaloneAnalysisAPISession {
        buildKtModuleProvider {
            platform = JvmPlatforms.defaultJvmPlatform
            addModule(buildKtSourceModule {
                moduleName = "Analysis module"
                platform = JvmPlatforms.defaultJvmPlatform
                addSourceRoots(listOf(Path("")))
            })
        }
    }

    val psiFile = KtPsiFactory(session.project).createFile("temp.kt", fileContent)

    @OptIn(KaExperimentalApi::class)
    psiFile.contextModule = session.modulesWithFiles.keys.first()

    val diagnostics = analyze(psiFile) {
        psiFile.collectDiagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS).map {
            it.defaultMessage
        }
    }

    println(diagnostics)
}

