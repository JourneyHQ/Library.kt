package dev.yuua.journeylib.discord.framework.command.router

import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionIndex
import dev.yuua.journeylib.discord.framework.command.scope.FrCmdScopeDB
import dev.yuua.journeylib.discord.framework.embed.LibEmbedColor
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class FrCmdRouter : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val command = try {
            FrCmdRouteDB.find(event.name, event.subcommandGroup, event.subcommandName)
        } catch (e: IllegalStateException) {
            event.replyEmbeds(
                EmbedBuilder()
                    .setTitle(":interrobang: Not Found")
                    .setDescription("```\nコマンドが見つかりませんでした。\n```")
                    .setColor(LibEmbedColor.failure)
                    .build()
            ).queue()
            return
        }

        val forbiddenEmbed = EmbedBuilder()
            .setTitle(":interrobang: Access Forbidden")
            .setDescription("```\nアクセスが拒否されました。\n```")
            .setColor(LibEmbedColor.failure)
            .build()

        for (frCmdScope in FrCmdScopeDB.find(command)) {
            //制限されていて、リストに含まれていない場合
            val guilds = frCmdScope.guilds
            if (guilds.isNotEmpty() && !guilds.contains(event.guild?.id)) {
                event.replyEmbeds(forbiddenEmbed).queue()
                return
            }

            val users = frCmdScope.users
            if (users.isNotEmpty() && !users.contains(event.user.id)) {
                event.replyEmbeds(forbiddenEmbed).queue()
                return
            }

            if (!frCmdScope.rule(event)) {
                event.replyEmbeds(forbiddenEmbed).queue()
                return
            }
        }

        for (frChecks in command.checks) {
            val checkResult = frChecks.check(event)
            if (!checkResult.passed) {
                event.replyEmbeds(
                    EmbedBuilder()
                        .setTitle(":interrobang: ${checkResult.type.code}")
                        .setDescription("```\n${checkResult.message}\n```")
                        .setColor(LibEmbedColor.failure)
                        .build()
                ).queue()
                return
            }
        }

        val options = mutableListOf<FrOptionIndex>()
        for (jdaOption in event.options) {
            val option = command.options.first { it.name == jdaOption.name }
            options.add(FrOptionIndex().fromOptionMapping(jdaOption, *option.subType))
        }

        command.function.execute(
            event.jda, event.guild, event.isFromGuild,
            event.channel, event.channelType, event.member,
            event.user, options, event
        )
    }
}
