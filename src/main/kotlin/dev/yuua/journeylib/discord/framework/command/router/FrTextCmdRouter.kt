package dev.yuua.journeylib.discord.framework.command.router

import dev.minn.jda.ktx.Embed
import dev.minn.jda.ktx.listener
import dev.minn.jda.ktx.messages.reply_
import dev.yuua.journeylib.discord.framework.FrExtention.Message.codeBlock
import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecksResultType
import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionLib.OptionAnalysisResultType.*
import dev.yuua.journeylib.discord.framework.command.event.FrCmdEvent
import dev.yuua.journeylib.discord.framework.command.event.FrEventLib.toFrCmdEvent
import dev.yuua.journeylib.discord.framework.command.event.FrJDAEvent
import dev.yuua.journeylib.discord.framework.command.scope.FrCmdScopeDB
import dev.yuua.journeylib.discord.framework.embed.FrColor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class FrTextCmdRouter(val jda: JDA) {
    init {
        jda.listener<MessageReceivedEvent> {
            val message = it.message

            if (!message.contentRaw.startsWith(";"))
                return@listener

            val commandAnalysis = try {
                FrTextAnalyzer.analyze(";", message.contentRaw)
            } catch (e: IllegalStateException) {
                message.reply_(
                    embed = Embed {
                        title = ":interrobang: Not Found"
                        description = "No such command was found!".codeBlock()
                        color = FrColor.failure.rgb
                    }
                ).queue()
                return@listener
            } catch (_: IllegalArgumentException) {
                return@listener
            }

            val command = commandAnalysis.first
            val optionAnalysisResult = commandAnalysis.second
            val options = optionAnalysisResult?.options ?: mutableListOf()
            val type = optionAnalysisResult?.type ?: Success

            when (type) {
                RequiredNotFound, MixedOption -> {
                    message.reply_(
                        embed = Embed {
                            title = ":interrobang: ${type.code}"
                            description = optionAnalysisResult?.message?.codeBlock()
                            color = FrColor.failure.rgb
                        }
                    ).queue()
                    return@listener
                }
                Success -> {
                    /* no-op */
                }
            }

            val frCmdEvent = it.toFrCmdEvent(options)

            for (frCmdScope in FrCmdScopeDB.find(command)) {
                //制限されていて、リストに含まれていない場合
                val guilds = frCmdScope.guilds
                val users = frCmdScope.users
                val checkResult = frCmdScope.checks?.check(frCmdEvent)

                val guildRestricted = guilds.isNotEmpty() && !guilds.contains(it.guild.id)
                val userRestricted = users.isNotEmpty() && !users.contains(it.author.id)
                val ruleRestricted =
                    if (checkResult != null)
                        checkResult.type != FrChecksResultType.PASSED
                    else false

                if (guildRestricted || userRestricted || ruleRestricted) {
                    message.reply_(
                        embed = Embed {
                            title = ":interrobang: ${checkResult?.message ?: "Access Forbidden"}"
                            description = "You do not have access to this command.".codeBlock()
                            color = FrColor.failure.rgb
                        }
                    ).queue()
                    return@listener
                }
            }

            for (frChecks in command.checks) {
                val checkResult = frChecks.check(frCmdEvent)
                if (checkResult.type != FrChecksResultType.PASSED) {
                    message.reply_(
                        embed = Embed {
                            title = ":interrobang: ${checkResult.type.code}"
                            description = checkResult.message.codeBlock()
                            color = FrColor.failure.rgb
                        }
                    ).queue()
                    return@listener
                }
            }

            if (command.textFunction == null) {
                message.reply_(
                    embed = Embed {
                        title = ":interrobang: Compatibility Error"
                        description = "This command is not compatible with text messages!".codeBlock()
                        color = FrColor.failure.rgb
                    }
                ).queue()
                return@listener
            }

            val jda = it.jda
            val guild = it.guild
            val isFromGuild = it.isFromGuild
            val isFromThread = it.isFromThread
            val channel = it.channel
            val channelType = it.channelType
            val member = it.member
            val author = it.author

            command.textFunction.execute(
                jda, guild, isFromGuild,
                isFromThread, channel, channelType,
                member, author, FrCmdEvent(
                    jda, guild, isFromGuild,
                    isFromThread, channel, channelType,
                    member, author, options, FrJDAEvent(null, it)
                )
            )
        }
    }
}
