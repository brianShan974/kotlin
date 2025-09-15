package org.jetbrains.kotlin

object ColorPrint {
    var enableColors: Boolean = true
    
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
    
    private const val ORANGE = "\u001B[38;5;208m"
    private const val PINK = "\u001B[38;5;205m"
    private const val LIME = "\u001B[38;5;154m"
    private const val TEAL = "\u001B[38;5;51m"
    private const val MAGENTA = "\u001B[38;5;201m"
    private const val GOLD = "\u001B[38;5;220m"
    private const val SILVER = "\u001B[38;5;7m"
    private const val CORAL = "\u001B[38;5;203m"
    
    private fun formatMessage(message: String, colorCode: String): String {
        return if (enableColors) "$colorCode$message$RESET" else message
    }

    fun red(message: String) = println(formatMessage(message, RED))
    fun green(message: String) = println(formatMessage(message, GREEN))
    fun yellow(message: String) = println(formatMessage(message, YELLOW))
    fun blue(message: String) = println(formatMessage(message, BLUE))
    fun purple(message: String) = println(formatMessage(message, PURPLE))
    fun cyan(message: String) = println(formatMessage(message, CYAN))
    
    fun brightRed(message: String) = println(formatMessage(message, BRIGHT_RED))
    fun brightGreen(message: String) = println(formatMessage(message, BRIGHT_GREEN))
    fun brightYellow(message: String) = println(formatMessage(message, BRIGHT_YELLOW))
    fun brightBlue(message: String) = println(formatMessage(message, BRIGHT_BLUE))
    fun brightPurple(message: String) = println(formatMessage(message, BRIGHT_PURPLE))
    fun brightCyan(message: String) = println(formatMessage(message, BRIGHT_CYAN))
    fun brightWhite(message: String) = println(formatMessage(message, BRIGHT_WHITE))
    
    fun orange(message: String) = println(formatMessage(message, ORANGE))
    fun pink(message: String) = println(formatMessage(message, PINK))
    fun lime(message: String) = println(formatMessage(message, LIME))
    fun teal(message: String) = println(formatMessage(message, TEAL))
    fun magenta(message: String) = println(formatMessage(message, MAGENTA))
    fun gold(message: String) = println(formatMessage(message, GOLD))
    fun silver(message: String) = println(formatMessage(message, SILVER))
    fun coral(message: String) = println(formatMessage(message, CORAL))
}
