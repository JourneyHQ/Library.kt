package dev.yuua.journeylib.discord.framework

object FrExtention {
    object Message {
        fun String.codeBlock(): String {
            return "```\n$this\n```"
        }

        fun String.codeBlock(lang: String): String {
            return "```$lang\n$this\n```"
        }
    }
}
