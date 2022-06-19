package dev.yuua.journeylib.qnortz.functions.command.router

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.reply_
import dev.yuua.journeylib.qnortz.QnortzColor
import dev.yuua.journeylib.qnortz.codeBlock
import dev.yuua.journeylib.qnortz.functions.command.CommandManager
import dev.yuua.journeylib.qnortz.functions.command.event.toUnified
import dev.yuua.journeylib.qnortz.functions.event.EventStruct
import dev.yuua.journeylib.qnortz.limit.check
import dev.yuua.journeylib.qnortz.rules.RulesResultType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * The router of text command.
 *
 * @param manager [CommandManager].
 */
class TextCommandReactor(private val manager: CommandManager) : EventStruct {
    override val script: JDA.() -> Unit = {
        listener<MessageReceivedEvent> {
            val message = it.message

            // analyze command string
            val commandAnalysis = try {
                analyzeTextCommand(";", message.contentRaw, manager.router)
            } catch (e: NoSuchElementException) {
                message.reply_(
                    embed = Embed {
                        title = ":interrobang: Not Found"
                        description = codeBlock("No such command was found!")
                        color = QnortzColor.Red.int()
                    }
                ).queue()
                return@listener
            } catch (_: IllegalArgumentException) {
                // not starts with prefix
                return@listener
            }

            val commandFunction = commandAnalysis.first
            val optionAnalysisResult = commandAnalysis.second
            val options = optionAnalysisResult.options
            val optionAnalysisResultMessage = optionAnalysisResult.message

            // check compatibility for text-command
            if (commandFunction.textFunction == null) {
                message.reply_(
                    embed = Embed {
                        title = ":interrobang: Compatibility Error"
                        description = codeBlock("This command is not compatible with text messages!")
                        color = QnortzColor.Red.int()
                    }
                ).queue()
                return@listener
            }

            // todo guild only function
            // todo ↑ filter? (bool)

            // cancel command execution if this channel cannot be accepted.
            val acceptedOn = commandFunction.acceptedOn
            if (acceptedOn.isNotEmpty() && !acceptedOn.contains(it.channelType)) {
                message.reply_(embed = invalidChannelTypeEmbed(acceptedOn)).queue()
                return@listener
            }

            // check options
            if (optionAnalysisResultMessage != null) {
                message.reply_(
                    embed = Embed {
                        title = ":interrobang: Invalid Option!"
                        description = codeBlock(optionAnalysisResultMessage)
                        color = QnortzColor.Red.int()
                    }
                ).queue()
                return@listener
            }

            val unifiedEvent = it.toUnified(options)

            // todo option to hide restriction reasons (configure in Qnortz)

            // limit : access control per package
            val limit = manager.limitRouter[commandFunction.packageName]
            val (passed, checkResultMessage) = limit.check(unifiedEvent, it.guild, it.channel, it.author, false)
            if (!passed) {
                message.reply_(
                    embed = accessForbiddenEmbed(checkResultMessage)
                ).queue()
                return@listener
            }

            // rule : access control per command
            for (rules in commandFunction.rules) {
                val ruleResult = rules.execute(unifiedEvent)
                if (ruleResult.type != RulesResultType.Passed) {
                    message.reply_(
                        embed = Embed {
                            title = ":interrobang: ${ruleResult.type.title}"
                            description = codeBlock(ruleResult.message ?: "No description provided.")
                            color = QnortzColor.Red.int()
                        }
                    ).queue()
                    return@listener
                }
            }

            commandFunction.textFunction.execute(unifiedEvent)
        }
    }
}
