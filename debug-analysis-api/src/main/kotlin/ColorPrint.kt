package org.jetbrains.kotlin

object ColorPrint {
    // ANSI颜色代码
    private const val RESET = "\u001B[0m"
    private const val RED = "\u001B[31m"
    private const val GREEN = "\u001B[32m"
    private const val YELLOW = "\u001B[33m"
    private const val BLUE = "\u001B[34m"
    private const val PURPLE = "\u001B[35m"
    private const val CYAN = "\u001B[36m"
    private const val BRIGHT_RED = "\u001B[91m"
    private const val BRIGHT_GREEN = "\u001B[92m"
    private const val BRIGHT_YELLOW = "\u001B[93m"
    private const val BRIGHT_BLUE = "\u001B[94m"
    private const val BRIGHT_PURPLE = "\u001B[95m"
    private const val BRIGHT_CYAN = "\u001B[96m"
    private const val BRIGHT_WHITE = "\u001B[97m"
    
    // 额外的颜色
    private const val ORANGE = "\u001B[38;5;208m"
    private const val PINK = "\u001B[38;5;205m"
    private const val LIME = "\u001B[38;5;154m"
    private const val TEAL = "\u001B[38;5;51m"
    private const val MAGENTA = "\u001B[38;5;201m"
    private const val GOLD = "\u001B[38;5;220m"
    private const val SILVER = "\u001B[38;5;7m"
    private const val CORAL = "\u001B[38;5;203m"

    fun red(message: String) = println("$RED$message$RESET")
    fun green(message: String) = println("$GREEN$message$RESET")
    fun yellow(message: String) = println("$YELLOW$message$RESET")
    fun blue(message: String) = println("$BLUE$message$RESET")
    fun purple(message: String) = println("$PURPLE$message$RESET")
    fun cyan(message: String) = println("$CYAN$message$RESET")
    
    fun brightRed(message: String) = println("$BRIGHT_RED$message$RESET")
    fun brightGreen(message: String) = println("$BRIGHT_GREEN$message$RESET")
    fun brightYellow(message: String) = println("$BRIGHT_YELLOW$message$RESET")
    fun brightBlue(message: String) = println("$BRIGHT_BLUE$message$RESET")
    fun brightPurple(message: String) = println("$BRIGHT_PURPLE$message$RESET")
    fun brightCyan(message: String) = println("$BRIGHT_CYAN$message$RESET")
    fun brightWhite(message: String) = println("$BRIGHT_WHITE$message$RESET")
    
    // 额外的颜色方法
    fun orange(message: String) = println("$ORANGE$message$RESET")
    fun pink(message: String) = println("$PINK$message$RESET")
    fun lime(message: String) = println("$LIME$message$RESET")
    fun teal(message: String) = println("$TEAL$message$RESET")
    fun magenta(message: String) = println("$MAGENTA$message$RESET")
    fun gold(message: String) = println("$GOLD$message$RESET")
    fun silver(message: String) = println("$SILVER$message$RESET")
    fun coral(message: String) = println("$CORAL$message$RESET")
}
