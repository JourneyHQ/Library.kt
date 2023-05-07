package dev.yuua.librarykt.journal

import com.github.ajalt.mordant.animation.Animation
import com.github.ajalt.mordant.animation.textAnimation
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal

class Journal(private val globalName: String? = null) {
    private val term = Terminal()

    enum class Symbols(val symbolString: String) {
        Info(brightMagenta("i")),
        Task(brightYellow(">")),
        Success(brightGreen("✓")),
        Failure(brightRed("✕")),
        Debug(brightBlue("#"))
    }

    operator fun get(symbol: Symbols, name: String = globalName ?: symbol.name) =
        JournalPrinter(term, symbol, name)
}

class JournalPrinter(private val term: Terminal, symbol: Journal.Symbols, val name: String) {
    private val prefix = "|${symbol.symbolString}|[${gray(name)}]"
    private val emptyPrefix = " ".repeat("|_|[$name]".length - 1) + "|"

    operator fun invoke(vararg contexts: String): JournalPrinter {
        for ((index, context) in contexts.withIndex()) {
            if (index == 0)
                term.println("$prefix $context")
            else term.println("$emptyPrefix $context")
        }

        return this
    }

    fun followUp(vararg contexts: String): JournalPrinter {
        for (context in contexts)
            term.println("$emptyPrefix $context")

        return this
    }

    operator fun get(vararg frames: String) =
        JournalAnimator(
            term.textAnimation { "$prefix ${frames[it]}" }
        )

}

class JournalAnimator(private val animation: Animation<Int>) {
    private var current = 0

    operator fun inc(): JournalAnimator {
        current++
        animation.update(current)

        return this
    }
}
