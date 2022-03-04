package dev.yuua.journeylib.discord.framework_text

import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionMapping

class CmdTextParser {
    //ex: cmd subcmd subcmdgroup option1:"aaa aaa" option2:"aaaaa"
    fun parse(content: String) {
        lateinit var command:String
        lateinit var subcommandGroup:String
        lateinit var subcommand:String
        lateinit var options:MutableList<OptionMapping>

    }
}