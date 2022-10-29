package dev.yuua.journeylib.qnortz.functions.command.router

import dev.minn.jda.ktx.events.listener
import dev.yuua.journeylib.qnortz.functions.command.CommandMethodType.SlashCommand
import dev.yuua.journeylib.qnortz.functions.command.CommandMethodType.TextCommand
import dev.yuua.journeylib.qnortz.functions.command.CommandManager
import dev.yuua.journeylib.qnortz.functions.command.event.toUnified
import dev.yuua.journeylib.qnortz.functions.event.EventStruct
import dev.yuua.journeylib.qnortz.limit.check
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

            val unifiedEvent = it.toUnified()

            // todo Duplicated code fragment

            // limit : access control per package
            val limit = manager.limitRouter[commandFunction.packageName]
            val (passed, checkResultMessage) =
                limit.check(unifiedEvent, it.guild, it.channel, it.user, false)
            if (!passed) {
                it.replyEmbeds(accessForbiddenEmbed(checkResultMessage)).setEphemeral(true).queue()
                return@listener
            }

            // rule : access control per command
            if (!commandFunction.filterEvent(unifiedEvent)) {
                it.replyEmbeds(accessForbiddenEmbed()).setEphemeral(true).queue()
                return@listener
            }

            when (commandFunction.type) {
                TextCommand -> commandFunction.textFunction!!.execute(unifiedEvent)
                SlashCommand -> commandFunction.slashFunction!!.execute(it)
            }
        }
    }
}
