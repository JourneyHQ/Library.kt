package dev.yuua.journeylib.qnortz.functions.command.event

import dev.minn.jda.ktx.generics.getChannel
import dev.minn.jda.ktx.interactions.commands.optionType
import dev.minn.jda.ktx.messages.*
import dev.yuua.journeylib.qnortz.functions.command.CommandMethodType
import dev.yuua.journeylib.qnortz.functions.command.builder.option.*
import dev.yuua.journeylib.qnortz.functions.command.event.unifiedReply.toUnifiedReplyActionDispatcher
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.utils.FileUpload

/**
 * Command interaction event which supports both [SlashCommandInteractionEvent] and [MessageReceivedEvent].
 *
 * @param jda JDA.
 * @param guild The guild which the event fired.
 * @param isFromGuild Whether the event fired on guild or not.
 * @param isFromThread Whether the event fired on thread or not.
 * @param channel The channel which the event fired.
 * @param channelType The type of [channel]
 * @param member The member who executed the command. If [isFromGuild] is false, this will be null.
 * @param user The user who executed the command.
 * @param jdaEvent Data class which stores original event.
 * @param options Options provided by user.
 */
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

    /**
     * Reply to user. (for Kotlin)
     *
     * @param content The string message to send.
     * @param embeds Multiple embeds to send.
     * @param files Multiple files to reply.
     */
    fun reply(
        content: String,
        embeds: Collection<MessageEmbed> = emptyList(),
        components: Collection<LayoutComponent> = emptyList(),
        files: Collection<FileUpload> = emptyList()
    ) = when (jdaEvent.type) {
        CommandMethodType.SlashCommand ->
            slash!!.reply_(content, embeds, components, files)
                .toUnifiedReplyActionDispatcher()

        CommandMethodType.TextCommand ->
            text!!.message.reply_(content, embeds, components, files)
                .toUnifiedReplyActionDispatcher()
    }

    /**
     * Reply to user with embeds.
     *
     * @param embed The single embed to send.
     * @param embeds Multiple embeds to send.
     */
    fun reply(embed: MessageEmbed, vararg embeds: MessageEmbed) =
        when (jdaEvent.type) {
            CommandMethodType.SlashCommand ->
                slash!!.reply_(embeds = embeds.toList())
                    .toUnifiedReplyActionDispatcher()

            CommandMethodType.TextCommand ->
                text!!.message.reply_(embeds = embeds.toList())
                    .toUnifiedReplyActionDispatcher()
        }

    /**
     * Reply to user with message.
     *
     * @param content The message to send.
     */
    fun reply(content: String) =
        when (jdaEvent.type) {
            CommandMethodType.SlashCommand ->
                slash!!.reply_(content).toUnifiedReplyActionDispatcher()

            CommandMethodType.TextCommand ->
                text!!.message.reply_(content).toUnifiedReplyActionDispatcher()
        }

    fun extractId(regex: Regex, value: String) =
        regex.find(value)?.groupValues?.get(1)
            ?: throw IllegalArgumentException("ID could not be extracted from the value provided!")

    /**
     * Returns option which has specified [name]. If no such option exists, returns null.
     *
     * @param T The type of the option.
     * @param name The name of the option.
     * @param guild The guild to resolve member id and role. This will be the guild which the event fired by default.
     */
    inline fun <reified T> optionOrNull(name: String, guild: Guild? = this.guild): T? {
        val option = options.firstOrNull { it.name == name }

        val optionValue = option?.value
        if (!matchType(optionType<T>(), optionValue))
            throw IllegalArgumentException("Type mismatch!")

        return typeCast<T>(optionValue, guild)
    }

    /**
     * Returns option which has specified [name]. If no such option exists, throws [IllegalArgumentException].
     *
     * @param T The type of the option.
     * @param name The name of the option.
     * @param guild The guild to resolve member id and role. This will be the guild which the event fired by default.
     */
    inline fun <reified T> option(name: String, guild: Guild? = this.guild) = optionOrNull<T>(name, guild)
        ?: throw IllegalArgumentException("Option value is null!")

    /**
     * Returns value which cast to [T] if possible.
     *
     * @param value The value to cast.
     * @param guild The guild to resolve member id and role.
     */
    inline fun <reified T> typeCast(value: Any?, guild: Guild?): T? {
        if (value == null) return value

        val string = value.toString()

        val returnValue: Any? = when (optionType<T>()) {
            OptionType.STRING -> string
            OptionType.INTEGER -> string.toIntOrNull()
            OptionType.BOOLEAN -> string.toBooleanStrictOrNull()
            OptionType.USER -> {
                //extract ID from the value provided (<@123456789...> -> 123456789...)
                val id = extractId(userRegex, value.toString())
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

        return returnValue as T?
    }
}

/**
 * Makes [UnifiedCommandInteractionEvent] from [SlashCommandInteractionEvent].
 */
fun SlashCommandInteractionEvent.toUnified() = UnifiedCommandInteractionEvent(
    jda, guild, isFromGuild, channelType.isThread,
    channel, channelType, member, user,
    CommandInteraction(this, null),
    options.map { UnifiedOption(it) }
)

/**
 * Makes [UnifiedCommandInteractionEvent] from [MessageReceivedEvent]
 */
fun MessageReceivedEvent.toUnified(options: List<UnifiedOption>) = UnifiedCommandInteractionEvent(
    jda, guild, isFromGuild, channelType.isThread,
    channel, channelType, member, author,
    CommandInteraction(null, this),
    options
)
