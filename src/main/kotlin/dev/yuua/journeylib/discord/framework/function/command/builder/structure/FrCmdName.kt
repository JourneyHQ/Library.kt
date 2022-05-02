package dev.yuua.journeylib.discord.framework.function.command.builder.structure

class FrCmdName(val name: String?, vararg val alias: String) {
    fun allNames(): MutableList<String?> {
        return mutableListOf(name, *alias)
    }
}
