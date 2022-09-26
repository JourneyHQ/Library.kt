package dev.yuua.journeylib.qnortz.filter

import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Filter<T>(filter: Filter<T>.() -> Unit) {
    init {
        this.apply(filter)
    }

    private val guilds = mutableListOf<String>()
    private val channels = mutableListOf<String>()
    private val users = mutableListOf<String>()
    private val roles = mutableListOf<String>()
    private val channelTypes = mutableListOf<ChannelType>()
    private var filterFunction: T.() -> Boolean = { true }

    private fun check(list: List<String>, id: String) = list.isEmpty() || list.contains(id)

    private fun checkGuild(guild: String) = check(guilds, guild)
    private fun checkChannel(channel: String) = check(channels, channel)
    private fun checkUser(user: String) = check(users, user)
    private fun checkRole(role: String) = check(roles, role)
    private fun checkChannelType(channelType: ChannelType) =
        channelTypes.isEmpty() && channelTypes.contains(channelType)

    fun guilds(vararg guildIdList: String) {//弾かれた場合のfunctionとか
        guilds.addAll(guildIdList)
        return
    }

    fun channels(vararg channelIdList: String) {
        channels.addAll(channelIdList)
        return
    }

    fun users(vararg userIdList: String) {
        users.addAll(userIdList)
        return
    }

    fun roles(vararg roleIdList: String) {
        roles.addAll(roleIdList)
        return
    }

    fun channelTypes(vararg channelTypeList: ChannelType) {
        channelTypes.addAll(channelTypeList)
        return
    }

    fun filterFunction(filterFunction: T.() -> Boolean) {
        this.filterFunction = filterFunction
    }

    operator fun invoke(event: T) = checkEvent(event)

    fun checkEvent(event: T): Boolean {
        return when (event) {
            is SlashCommandInteractionEvent -> listOf(
                checkGuild(event.guild?.id ?: "%GUILD_NULL%"),
                checkChannel(event.channel.id),
                checkUser(event.user.id),
                checkRole(event.member?.roles?.firstOrNull()?.id ?: "%ROLE_NULL%"),
                checkChannelType(event.channelType)
            ).all { it }

            else -> {
                false
            }
        }
    }
}
