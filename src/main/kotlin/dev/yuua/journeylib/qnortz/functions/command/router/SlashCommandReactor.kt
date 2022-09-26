package dev.yuua.journeylib.qnortz.functions.command.router

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.messages.Embed
import dev.yuua.journeylib.qnortz.QnortzColor
import dev.yuua.journeylib.qnortz.codeBlock
import dev.yuua.journeylib.qnortz.functions.command.CommandFromType.SlashCommand
import dev.yuua.journeylib.qnortz.functions.command.CommandFromType.TextCommand
import dev.yuua.journeylib.qnortz.functions.command.CommandManager
import dev.yuua.journeylib.qnortz.functions.command.event.toUnified
import dev.yuua.journeylib.qnortz.functions.event.EventStruct
import dev.yuua.journeylib.qnortz.limit.check
import dev.yuua.journeylib.qnortz.rules.RulesResultType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * The router of slash command.
 *
 * @param manager [CommandManager].
 */
class SlashCommandReactor(private val manager: CommandManager) : EventStruct {
    override val script: JDA.() -> Unit = {
        listener<SlashCommandInteractionEvent> {
            val commandFunction = manager.router[CommandRoute(
                it.name,
                it.subcommandGroup,
                it.subcommandName
            )]

            // cancel command execution if this channel cannot be accepted.
            val acceptedOn = commandFunction.acceptedOn
            if (acceptedOn.isNotEmpty() && !acceptedOn.contains(it.channelType)) {
                it.replyEmbeds(invalidChannelTypeEmbed(acceptedOn)).queue()
                return@listener
            }

            val unifiedEvent = it.toUnified()

            // todo Duplicated code fragment

            // limit : access control per package
            val limit = manager.limitRouter[commandFunction.packageName]
            val (passed, checkResultMessage) =
                limit.check(unifiedEvent, it.guild, it.channel, it.user, false)
            if (!passed) {
                it.replyEmbeds(accessForbiddenEmbed(checkResultMessage)).queue()
                return@listener
            }

            // rule : access control per command
            for (rules in commandFunction.rules) {
                val ruleResult = rules.execute(unifiedEvent)
                if (ruleResult.type != RulesResultType.Passed) {
                    it.replyEmbeds(Embed {
                        title = ":interrobang: ${ruleResult.type.title}"
                        description = codeBlock(ruleResult.message ?: "No description provided.")
                        color = QnortzColor.Red.int()
                    }).queue()
                    return@listener
                }
            }

            when (commandFunction.type) {
                TextCommand -> commandFunction.textFunction!!.execute(unifiedEvent)
                SlashCommand -> commandFunction.slashFunction!!.execute(it)
            }
        }
    }
}
