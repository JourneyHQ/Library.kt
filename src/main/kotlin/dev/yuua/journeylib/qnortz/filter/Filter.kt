package dev.yuua.journeylib.qnortz.filter

import dev.yuua.journeylib.qnortz.code
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

class Filter<T>(
    val guildIds: List<String> = emptyList(),
    val channelIds: List<String> = emptyList(),
    val userIds: List<String> = emptyList(),
    val roleIds: List<String> = emptyList(),
    val permissions: List<Permission> = emptyList(),
    val channelTypes: List<ChannelType> = emptyList(),
    val guildOnly: Boolean = false,
    val filterScript: T.() -> Pair<Boolean, String> = { true to "" }
) {
    val defaultMemberPermissions = DefaultMemberPermissions.enabledFor(permissions)

    private infix fun <T> List<T>.containOrEmpty(id: T?) = this.isEmpty() || this.contains(id)

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

    /**
     * Checks if [event] satisfies requirements of [Filter].
     * @param event Event to check
     * @return List of strings which contain messages to show to users when any requirements are not satisfied.
     *         Empty when all checks have passed.
     */
    fun check(event: T): List<String> {
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

        fun notAvailableOnThis(string: String) = "Not available on this $string."
        val youDontHavePermissions = "You don't have permissions."

        return hashMapOf(
            guildIds containOrEmpty guildId to notAvailableOnThis("guild"),
            channelIds containOrEmpty channelId to notAvailableOnThis("channel"),
            userIds containOrEmpty userId to youDontHavePermissions,
            (roleIds.isEmpty() || userRoleIds.any { roleIds.contains(it) }) to youDontHavePermissions,
            (permissions.isEmpty() || userPermissions.containsAll(permissions)) to youDontHavePermissions,
            channelTypes containOrEmpty channelType to "${notAvailableOnThis("type of channel")} Available on ${code(channelTypes.joinToString(", "))}",
            !(guildOnly && !isGuild) to "Only available on guilds.",
            filterScript(event)
        ).filter { !it.key }.map { it.value }
    }
}
