package dev.yuua.journeylib.qnortz.functions.command.router

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.messages.Embed
import dev.yuua.journeylib.qnortz.QnortzColor
import dev.yuua.journeylib.qnortz.codeBlock
import dev.yuua.journeylib.qnortz.functions.command.CommandManager
import dev.yuua.journeylib.qnortz.functions.command.event.toUnified
import dev.yuua.journeylib.qnortz.functions.event.EventStruct
import dev.yuua.journeylib.qnortz.limit.check
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
                message.replyEmbeds(Embed {
                    title = ":interrobang: Not Found"
                    description = codeBlock("No such command was found!")
                    color = QnortzColor.Red.int()
                }).queue()
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
                message.replyEmbeds(Embed {
                    title = ":interrobang: Compatibility Error"
                    description = codeBlock("This command is not compatible with text messages!")
                    color = QnortzColor.Red.int()
                }).queue()
                return@listener
            }

            // check options
            if (optionAnalysisResultMessage != null) {
                message.replyEmbeds(Embed {
                    title = ":interrobang: Invalid Option!"
                    description = codeBlock(optionAnalysisResultMessage)
                    color = QnortzColor.Red.int()
                }).queue()
                return@listener
            }

            val unifiedEvent = it.toUnified(options)

            // limit : access control per package
            val limit = manager.limitRouter[commandFunction.packageName]
            val (passed, checkResultMessage) = limit.check(unifiedEvent, it.guild, it.channel, it.author, false)
            if (!passed) {
                message.replyEmbeds(accessForbiddenEmbed(checkResultMessage)).queue()
                return@listener
            }

            if (!commandFunction.checkFilter(unifiedEvent)) {
                unifiedEvent.reply(accessForbiddenEmbed()).queue()
                return@listener
            }

            // todo option to hide restriction reasons (configure in Qnortz)

            commandFunction.textFunction.execute(unifiedEvent)
        }
    }
}
