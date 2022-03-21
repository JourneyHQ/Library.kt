package dev.yuua.journeylib.discord.framework.command.builder.structure

import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecks
import dev.yuua.journeylib.discord.framework.command.builder.function.FrFunction
import dev.yuua.journeylib.discord.framework.command.builder.option.FrOption
import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionLib.toJDAOptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

class FrSubcmd(val name: String, val details: String) {

    val jdaSubcmdData = SubcommandData(name, details)

    lateinit var function: FrFunction
    val options = mutableListOf<FrOption>()
    lateinit var checks: FrChecks

    fun addOption(vararg options: FrOption): FrSubcmd {
        this.options.addAll(options)
        jdaSubcmdData.addOptions(options.map { it.toJDAOptionData() })
        return this
    }

    fun setFunction(function: FrFunction): FrSubcmd {
        this.function = function
        return this
    }

    fun addChecks(checks: FrChecks): FrSubcmd {
        this.checks = checks
        return this
    }
}
