package dev.yuua.journeylib.universal

object LibString {
    fun String.removeQuote(): String {
        return if (this.startsWith("\"") && this.endsWith("\""))
            this.substring(1, this.length - 1)
         else this
    }
}
