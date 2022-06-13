package dev.yuua.journeylib.qnortz.functions.command.event

import dev.minn.jda.ktx.generics.getChannel
import dev.minn.jda.ktx.interactions.commands.optionType
import dev.minn.jda.ktx.messages.*
import dev.yuua.journeylib.qnortz.functions.command.CommandFromType
import dev.yuua.journeylib.qnortz.functions.command.builder.option.*
import dev.yuua.journeylib.qnortz.functions.command.event.unifiedReply.toUnifiedReplyActionDispatcher
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType

data class UnifiedCommandInteractionEvent(
    val jda: JDA,
    val guild: Guild?,
    val isFromGuild: Boolean,
    val isFromThread: Boolean,
    val channel: MessageChannel,
    val channelType: ChannelType,
    val member: Member?,
    val user: User,
    val jdaEvent: CommandInteraction,
    val options: List<UnifiedOption>
) {
    private val slash = jdaEvent.slashCommandInteractionEvent
    private val text = jdaEvent.messageReceivedEvent

    fun reply(
        content: String? = null,
        embed: MessageEmbed? = null,
        embeds: Embeds = emptyList(),
        components: Components = emptyList(),
        file: NamedFile? = null,
        files: Files = emptyList()
    ) = when (jdaEvent.type) {
        CommandFromType.SlashCommand ->
            slash!!.reply_(content, embed, embeds, components, file, files)
                .toUnifiedReplyActionDispatcher()

        CommandFromType.TextCommand ->
            text!!.message.reply_(content, embed, embeds, components, file, files)
                .toUnifiedReplyActionDispatcher()
    }

    fun reply(message: MessageEmbed, vararg messages: MessageEmbed) =
        when (jdaEvent.type) {
            CommandFromType.SlashCommand ->
                slash!!.reply_(embed = message, embeds = messages.toList())
                    .toUnifiedReplyActionDispatcher()

            CommandFromType.TextCommand ->
                text!!.message.reply_(embed = message, embeds = messages.toList())
                    .toUnifiedReplyActionDispatcher()
        }

    fun reply(content: String) =
        when (jdaEvent.type) {
            CommandFromType.SlashCommand ->
                slash!!.reply_(content).toUnifiedReplyActionDispatcher()

            CommandFromType.TextCommand ->
                text!!.message.reply_(content).toUnifiedReplyActionDispatcher()
        }


    fun extractId(regex: Regex, value: String) =
        regex.find(value)?.groupValues?.get(1)
            ?: throw IllegalArgumentException("ID could not be extracted from the value provided!")


    // switch method by option is required or not. option / optionOrNull

    inline fun <reified T> optionOrNull(name: String, guild: Guild? = this.guild): T? {
        val option = options.firstOrNull { it.name == name }

        val optionValue = option?.value
        if (!matchType(optionType<T>(), optionValue))
            throw IllegalArgumentException("Type mismatch!")

        return typeCast<T>(optionValue, guild)
    }

    inline fun <reified T> option(name: String, guild: Guild? = this.guild) = optionOrNull<T>(name, guild)
        ?: throw IllegalArgumentException("Option value is null!")

    inline fun <reified T> typeCast(value: Any?, guild: Guild?): T? {
        if (value == null) return value

        println(value)

        val string = value.toString()

        val returnValue: Any? = when (optionType<T>()) {
            OptionType.STRING -> string
            OptionType.INTEGER -> string.toIntOrNull()
            OptionType.BOOLEAN -> string.toBooleanStrictOrNull()
            OptionType.USER -> {
                //extract ID from the value provided (<@123456789...> -> 123456789...)
                val id = extractId(userRegex, value.toString())
                println(guild?.getMemberById(id))
                when (T::class) {
                    User::class -> jda.getUserById(id)
                    Member::class -> guild?.getMemberById(id)
                    else -> throw IllegalStateException("OptionType - Class mismatch!")
                }
            }

            OptionType.CHANNEL -> {
                val id = extractId(channelRegex, string)
                jda.getChannel(id)
            }

            OptionType.ROLE -> {
                val id = extractId(roleRegex, string)
                jda.getRoleById(id)
            }

            OptionType.MENTIONABLE -> {
                val id = extractId(mentionableRegex, string)

                listOf(
                    jda.getRoleById(id),
                    jda.getUserById(id),
                    guild?.getMemberById(id)
                ).first { it != null }
            }

            OptionType.NUMBER -> when (T::class) {
                Long::class -> string.toLongOrNull()
                Double::class -> string.toDoubleOrNull()
                else -> throw IllegalStateException("OptionType - Class mismatch!")
            }

            else -> throw IllegalStateException("OptionType - Class mismatch!")
        }

        println(returnValue)

        return returnValue as T?
    }
}

fun SlashCommandInteractionEvent.toUnified() = UnifiedCommandInteractionEvent(
    jda, guild, isFromGuild, channelType.isThread,
    channel, channelType, member, user,
    CommandInteraction(this, null),
    options.map { UnifiedOption(it) }
)


fun MessageReceivedEvent.toUnified(options: List<UnifiedOption>) = UnifiedCommandInteractionEvent(
    jda, guild, isFromGuild, channelType.isThread,
    channel, channelType, member, author,
    CommandInteraction(null, this),
    options
)
