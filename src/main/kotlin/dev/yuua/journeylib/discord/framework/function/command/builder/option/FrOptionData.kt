package dev.yuua.journeylib.discord.framework.function.command.builder.option

import dev.minn.jda.ktx.interactions.Option
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * Generate [OptionData].
 * @param name Name of [OptionData].
 * @param description Description of [OptionData].
 * @param required Whether the option is required or not.
 * @param autocomplete Whether the option supports autocomplete or not.
 * @param builder Code to adapt to [OptionData].
 */
inline fun <reified T> FrOption(
    name: String,
    description: String,
    required: Boolean = false,
    autocomplete: Boolean = false,
    builder: OptionData.() -> Unit = {}
): OptionData {
    val jdaOption = Option<T>(name, description, required, autocomplete, builder)

    if (jdaOption.type == OptionType.CHANNEL && jdaOption.channelTypes.isEmpty()) {
        val channelTypes = when (T::class) {
            TextChannel::class -> listOf(ChannelType.TEXT)
            PrivateChannel::class -> listOf(ChannelType.PRIVATE) //多分仕様的に不可能
            VoiceChannel::class -> listOf(ChannelType.VOICE)
            StageChannel::class -> listOf(ChannelType.STAGE)
            AudioChannel::class -> listOf(ChannelType.VOICE, ChannelType.STAGE)
            Category::class -> listOf(ChannelType.CATEGORY)
            NewsChannel::class -> listOf(ChannelType.NEWS)
            ThreadChannel::class -> listOf(
                ChannelType.GUILD_PUBLIC_THREAD,
                ChannelType.GUILD_PRIVATE_THREAD,
                ChannelType.GUILD_NEWS_THREAD
            )
            else -> emptyList()
        }
        jdaOption.setChannelTypes(channelTypes)
    }

    return jdaOption
}
