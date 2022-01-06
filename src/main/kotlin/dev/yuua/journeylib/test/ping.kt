package dev.yuua.journeylib.test

import dev.yuua.journeylib.discord.framework.command.CmdExtension.build
import dev.yuua.journeylib.discord.framework.command.CmdExtension.setCmdFunction
import dev.yuua.journeylib.discord.framework.command.CmdExtension.setPermission
import dev.yuua.journeylib.discord.framework.command.CmdSubstrate
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

class ping : CmdSubstrate {
    override fun data(): CommandData = CommandData("powa", "powa!")
        .addSubcommandGroups(
            SubcommandGroupData("powagroup", "powa!")
                .addSubcommands(
                    SubcommandData("powasub", "powasub!")
                        .setCmdFunction(this::sample)
                        .setPermission { true }
                ),
            SubcommandGroupData("notpowagroup", "powa!")
                .addSubcommands(
                    SubcommandData("notpowasub", "powasub!")
                        .setCmdFunction(this::sample),
                    SubcommandData("powasub", "powasub!")
                        .setCmdFunction(this::sample)
                ),
        ).build()

    private fun sample(
        jda: JDA, guild: Guild?, isFromGuild: Boolean,
        channel: MessageChannel, type: ChannelType,
        member: Member?, user: User,
        event: SlashCommandEvent
    ) {
        event.reply("powa!!").setEphemeral(true).queue()
    }
}