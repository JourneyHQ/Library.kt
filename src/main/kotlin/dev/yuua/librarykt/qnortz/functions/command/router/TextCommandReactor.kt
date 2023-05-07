package dev.yuua.librarykt.qnortz.functions.command.router

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.messages.Embed
import dev.yuua.librarykt.qnortz.QnortzColor
import dev.yuua.librarykt.qnortz.codeBlock
import dev.yuua.librarykt.qnortz.functions.command.CommandManager
import dev.yuua.librarykt.qnortz.functions.command.event.toUnified
import dev.yuua.librarykt.qnortz.functions.event.EventStruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * The router of text command.
 *
 * @param manager [CommandManager].
 */
class TextCommandReactor(private val manager: CommandManager) : EventStruct {
    override val script: JDA.() -> Unit = {
        listener<MessageReceivedEvent> { event ->
            val message = event.message

            // analyze command string
            val (commandRoute, commandFunction, optionAnalysisResult) = try {
                analyzeTextCommand(";", message.contentRaw, manager.router)
            } catch (_: NoSuchElementException) {
                // command not found
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

            val unifiedEvent = event.toUnified(options)

            // check filter
            val filters = manager.packageFilterRouter.findAll(manager.findRoutePackage(commandRoute))
            val packageFilterMessages = filters.map { it.check(unifiedEvent) }.flatten().distinct()
            val commandFilterMessages = commandFunction.checkFilter(unifiedEvent)
            val flattenedMessages = (packageFilterMessages + commandFilterMessages).joinToString("\n") { "* $it" }

            if (packageFilterMessages.isNotEmpty() || commandFilterMessages.isNotEmpty()) {
                message.replyEmbeds(accessForbiddenEmbed(flattenedMessages)).queue()
                return@listener
            }

            // todo option to hide restriction reasons (configure in Qnortz)
            commandFunction.textFunction.execute(unifiedEvent)
        }
    }
}
