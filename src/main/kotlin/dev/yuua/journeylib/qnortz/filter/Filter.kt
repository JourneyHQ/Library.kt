package dev.yuua.journeylib.qnortz.filter

import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.functions.command.event.toUnified
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

class Filter<T>(
    val guildIds: List<String> = emptyList(),
    val channelIds: List<String> = emptyList(),
    val userIds: List<String> = emptyList(),
    val roleIds: List<String> = emptyList(),
    val permissions: List<Permission> = emptyList(),
    val channelTypes: List<ChannelType> = emptyList(),
    val guildOnly: Boolean = false,
    val filterScript: T.() -> Boolean = { true }
) {
    val defaultMemberPermissions = DefaultMemberPermissions.enabledFor(permissions)

    private fun <T> containOrEmpty(list: List<T>, id: T?) = list.isEmpty() || list.contains(id)

    data class FilterElements(
        val guildId: String?,
        val channelId: String?,
        val userId: String?,
        val roleIds: List<String>,
        val permissions: List<Permission>,
        val channelType: ChannelType,
        val isGuild: Boolean
    )

    // todo support more event types
    fun checkEvent(event: T): Boolean {
        val unifiedCommandEvent = when (event) {
            is UnifiedCommandInteractionEvent -> event
            else -> null
        }

        val (guildId, channelId, userId, userRoleIds, userPermissions, channelType, isGuild) =
            if (unifiedCommandEvent != null)
                FilterElements(
                    unifiedCommandEvent.guild?.id, unifiedCommandEvent.channel.id, unifiedCommandEvent.user.id,
                    unifiedCommandEvent.member?.roles?.map { it.id } ?: emptyList(),
                    unifiedCommandEvent.member?.permissions?.toList() ?: emptyList(),
                    unifiedCommandEvent.channelType, unifiedCommandEvent.isFromGuild
                )
            else throw UnsupportedOperationException()

        return listOf(
            containOrEmpty(guildIds, guildId),
            containOrEmpty(channelIds, channelId),
            containOrEmpty(userIds, userId),
            userRoleIds.map { roleIds.contains(it) }.any { it },
            userPermissions.containsAll(permissions),
            containOrEmpty(channelTypes, channelType),
            !(guildOnly && !isGuild),
            filterScript(event)
        ).all { it }
    }
}
