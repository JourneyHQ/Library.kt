package dev.yuua.journeylib.qnortz.functions.command.builder.option

import dev.minn.jda.ktx.interactions.commands.Option
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

object OptionTool {
    inline fun <reified T> toChannelTypes(): List<ChannelType> {
        return when (T::class) {
            TextChannel::class -> listOf(ChannelType.TEXT)
            PrivateChannel::class -> listOf(ChannelType.PRIVATE) // maybe impossible
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
    }

    inline fun <reified T> option(
        name: String,
        description: String,
        required: Boolean = false,
        autocomplete: Boolean = false,
        builder: OptionData.() -> Unit = {}
    ): OptionData {
        val jdaOption = Option<T>(name, description, required, autocomplete, builder)

        // OptionType is CHANNEL and ChannelType is empty
        if (jdaOption.type == OptionType.CHANNEL
            && jdaOption.channelTypes.isEmpty()) {
            jdaOption.setChannelTypes(toChannelTypes<T>())
        }

        return jdaOption
    }
}
