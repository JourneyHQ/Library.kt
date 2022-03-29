package dev.yuua.journeylib.discord.framework.command.builder.option

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.OptionType.*
import java.io.File
import kotlin.reflect.KClass


object FrOptionTypeLib {
    fun toOptionType(cls: KClass<*>): OptionType {
        return when (cls) {
            Float::class, Double::class -> NUMBER
            Integer::class, Long::class, Short::class, Byte::class -> INTEGER
            String::class -> STRING
            User::class, Member::class -> USER
            Role::class -> ROLE
            Boolean::class -> BOOLEAN
            File::class, Attachment::class -> ATTACHMENT
            else -> when {
                Channel::class.java.isAssignableFrom(cls::class.java) -> CHANNEL
                IMentionable::class.java.isAssignableFrom(cls::class.java) -> MENTIONABLE
                else -> UNKNOWN
            }
        }
    }

    fun toClassType(optionType: OptionType): List<KClass<*>> {
        return when (optionType) {
            STRING -> listOf(String::class)
            INTEGER -> listOf(Long::class)
            BOOLEAN -> listOf(Boolean::class)
            USER -> listOf(User::class, Member::class)
            CHANNEL -> listOf(
                GuildMessageChannel::class,
                MessageChannel::class,
                NewsChannel::class,
                PrivateChannel::class,
                TextChannel::class,
                ThreadChannel::class,
                VoiceChannel::class
            )
            ROLE -> listOf(Role::class)
            MENTIONABLE -> listOf(Role::class, User::class, Member::class)
            NUMBER -> listOf(Long::class, Double::class)
            ATTACHMENT -> listOf(Attachment::class)
            else -> listOf(Any::class)
        }
    }
}
