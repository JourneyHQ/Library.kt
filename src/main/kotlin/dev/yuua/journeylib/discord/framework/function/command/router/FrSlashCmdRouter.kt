package dev.yuua.journeylib.discord.framework.function.command.router

import dev.minn.jda.ktx.Embed
import dev.minn.jda.ktx.listener
import dev.minn.jda.ktx.messages.reply_
import dev.yuua.journeylib.discord.framework.FrExtention.Message.codeBlock
import dev.yuua.journeylib.discord.framework.Framework
import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrChecksResultType
import dev.yuua.journeylib.discord.framework.function.command.event.FrEventLib.toFrCmdEvent
import dev.yuua.journeylib.discord.framework.function.scope.FrCmdScopeDB
import dev.yuua.journeylib.discord.framework.embed.FrColor
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class FrSlashCmdRouter(private val framework: Framework) {
    init {
        val jda = framework.jda
        jda.listener<SlashCommandInteractionEvent> {
            val command = try {
                FrCmdRouteDB.find(it.name, it.subcommandGroup, it.subcommandName)
            } catch (e: IllegalStateException) {
                it.reply_(
                    embed = Embed {
                        title = ":interrobang: Not Found"
                        description = "No such command was found!".codeBlock()
                        color = FrColor.failure.rgb
                    },
                    ephemeral = true
                ).queue()
                return@listener
            }

            val frCmdEvent = it.toFrCmdEvent()

            val frCmdScope = FrCmdScopeDB(framework).find(command)
            if (frCmdScope != null) {
                //制限されていて、リストに含まれていない場合
                val guilds = frCmdScope.guilds
                val users = frCmdScope.users
                val checkResult = frCmdScope.checks?.check(frCmdEvent)

                val guildRestricted = guilds.isNotEmpty() && !guilds.contains(it.guild?.id)
                val userRestricted = users.isNotEmpty() && !users.contains(it.user.id)
                val ruleRestricted =
                    if (checkResult != null)
                        checkResult.type != FrChecksResultType.PASSED
                    else false

                if (guildRestricted || userRestricted || ruleRestricted) {
                    it.reply_(
                        embed = Embed {
                            title = ":interrobang: ${checkResult?.message ?: "Access Forbidden"}"
                            description = "You do not have access to this command.".codeBlock()
                            color = FrColor.failure.rgb
                        },
                        ephemeral = true
                    ).queue()
                    return@listener
                }
            }

            for (frChecks in command.checks) {
                val checkResult = frChecks.check(frCmdEvent)
                if (checkResult.type != FrChecksResultType.PASSED) {
                    it.reply_(
                        embed = Embed {
                            title = ":interrobang: ${checkResult.type.code}"
                            description = checkResult.message?.codeBlock()
                            color = FrColor.failure.rgb
                        },
                        ephemeral = true
                    ).queue()
                    return@listener
                }
            }

            val isFromThread = listOf(
                ChannelType.GUILD_NEWS_THREAD,
                ChannelType.GUILD_PRIVATE_THREAD,
                ChannelType.GUILD_PUBLIC_THREAD
            ).contains(it.channelType)

            val jda = it.jda
            val guild = it.guild
            val isFromGuild = it.isFromGuild
            val channel = it.channel
            val channelType = it.channelType
            val member = it.member
            val user = it.user

            when {
                command.function != null -> {
                    command.function.execute(
                        jda, guild, isFromGuild,
                        isFromThread, channel, channelType,
                        member, user, it
                    )
                }
                command.textFunction != null -> {
                    command.textFunction.execute(
                        jda, guild, isFromGuild,
                        isFromThread, channel, channelType,
                        member, user, frCmdEvent
                    )
                }
            }
        }
    }
}
