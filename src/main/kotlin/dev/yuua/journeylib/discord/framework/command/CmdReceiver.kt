package dev.yuua.journeylib.discord.framework.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CmdReceiver : ListenerAdapter() {
    override fun onSlashCommand(event: SlashCommandEvent) {
        val cmdIndex: CmdRouter.CmdIndex? =
            CmdRouter.RouteData[CmdRouter.CmdIdentifier(event.name, event.subcommandGroup, event.subcommandName)]

        if (cmdIndex == null) {
            event.replyEmbeds(
                EmbedBuilder()
                    .setTitle(":interrobang: NOT FOUND")
                    .setDescription("コマンドが見つかりませんでした。")
                    .build()
            ).queue()
            return
        }

        for (cmdPermission in cmdIndex.permission)
            if (!cmdPermission.check(event)) {
                event.replyEmbeds(
                    EmbedBuilder()
                        .setTitle(":no_entry_sign: NOT PERMITTED")
                        .setDescription("この操作は許可されていません！")
                        .build()
                ).queue()
                return
            }

        cmdIndex.cmdFunction.execute(
            event.jda, event.guild, event.isFromGuild,
            event.channel, event.channelType, event.member,
            event.user, event
        )
    }
}