package dev.yuua.journeylib.discord.framework.command

import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface CmdSubstrate {
    fun data(): CommandData
}