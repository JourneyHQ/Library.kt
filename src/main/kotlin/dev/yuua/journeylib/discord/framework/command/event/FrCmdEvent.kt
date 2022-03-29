package dev.yuua.journeylib.discord.framework.command.event

import dev.minn.jda.ktx.getChannel
import dev.minn.jda.ktx.interactions.getOption
import dev.minn.jda.ktx.interactions.optionType
import dev.minn.jda.ktx.messages.*
import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionIndex
import dev.yuua.journeylib.discord.framework.command.event.FrEventLib.toFrReplyAction
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.interactions.commands.OptionType

class FrCmdEvent(
    val jda: JDA,
    val guild: Guild?,
    val isFromGuild: Boolean,
    val isFromThread: Boolean,
    val channel: MessageChannel,
    val channelType: ChannelType,
    val member: Member?,
    val user: User,
    val options: MutableList<FrOptionIndex>,
    val jdaEvent: FrJDAEvent
) {
    fun reply(
        content: String? = null,
        embed: MessageEmbed? = null,
        embeds: Embeds = emptyList(),
        components: Components = emptyList(),
        file: NamedFile? = null,
        files: Files = emptyList()
    ): FrReplyAction {
        return when (jdaEvent.type) {
            FrCmdType.SLASH ->
                jdaEvent.slash!!.reply_(content, embed, embeds, components, file, files).toFrReplyAction()
            FrCmdType.TEXT ->
                jdaEvent.text!!.message.reply_(content, embed, embeds, components, file, files).toFrReplyAction()
        }
    }

    fun reply(message: MessageEmbed, vararg messages: MessageEmbed): FrReplyAction {
        return when (jdaEvent.type) {
            FrCmdType.SLASH ->
                jdaEvent.slash!!.reply_(embed = message, embeds = messages.toList()).toFrReplyAction()
            FrCmdType.TEXT ->
                jdaEvent.text!!.message.reply_(embed = message, embeds = messages.toList()).toFrReplyAction()
        }
    }

    fun reply(content: String): FrReplyAction {
        return when (jdaEvent.type) {
            FrCmdType.SLASH ->
                jdaEvent.slash!!.reply_(content).toFrReplyAction()
            FrCmdType.TEXT ->
                jdaEvent.text!!.message.reply_(content).toFrReplyAction()
        }
    }

    fun extractId(regex: Regex, value: String): String {
        println(value)
        return regex.find(value)?.groupValues?.get(1)
            ?: throw IllegalArgumentException("ID could not be extracted from the value provided!")
    }

    inline fun <reified T> option(name: String, guild: Guild? = this.guild): T? {
        val option = options.firstOrNull { it.name == name }

        val optionValue = option?.value
        val returnValue = when (jdaEvent.type) {
            FrCmdType.SLASH -> jdaEvent.slash!!.getOption<T>(name)
            FrCmdType.TEXT -> {
                when (optionType<T>()) {
                    OptionType.STRING -> optionValue
                    OptionType.INTEGER -> optionValue.toString().toIntOrNull()
                    OptionType.BOOLEAN ->
                        when (optionValue.toString().lowercase()) {
                            "yes", "true" -> true
                            "no", "false" -> false
                            else -> null
                        }
                    OptionType.USER -> {
                        //extract ID from the value provided (<@123456789...> -> 123456789...)
                        val id = extractId(Regex("<@!?([0-9]+)>"), optionValue.toString())

                        when (T::class) {
                            User::class -> {
                                jda.getUserById(id)
                            }
                            Member::class -> {
                                guild?.getMemberById(id)
                            }
                            else -> throw IllegalStateException("OptionType - Class mismatch!")
                        }
                    }
                    OptionType.CHANNEL -> {
                        val id = extractId(Regex("<#([0-9]+)>"), optionValue.toString())

                        jda.getChannel(id)
                    }
                    OptionType.ROLE -> {
                        val id = extractId(Regex("<@&([0-9]+)>"), optionValue.toString())

                        jda.getRoleById(id)
                    }
                    OptionType.MENTIONABLE -> {
                        val id = extractId(Regex("<@[&!]?([0-9]+)>"), optionValue.toString())

                        listOf(
                            jda.getRoleById(id),
                            jda.getUserById(id),
                            guild?.getMemberById(id)
                        ).first { it != null }
                    }
                    OptionType.NUMBER -> when (T::class) {
                        Long::class -> optionValue.toString().toLongOrNull()
                        Double::class -> optionValue.toString().toDoubleOrNull()
                        else -> throw IllegalStateException("OptionType - Class mismatch!")
                    }
                    else -> option?.value as T
                }
            }
        }

        return returnValue as T
    }
}
