package dev.yuua.librarykt.universal

object LibString {
    fun String.trimQuote(): String {
        return if (this.startsWith("\"") && this.endsWith("\""))
            this.substring(1, this.length - 1)
         else this
    }
}
