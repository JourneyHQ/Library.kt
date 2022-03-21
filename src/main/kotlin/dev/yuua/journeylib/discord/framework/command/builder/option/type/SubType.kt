package dev.yuua.journeylib.discord.framework.command.builder.option.type


enum class SubType(val regex: Regex?) {
    String(null),
    Boolean(Regex("(true|false|yes|no)")),
    User(Regex("<@([0-9]+)>")),
    Member(Regex("<@!?([0-9]+)>")),
    GuildChannel(Regex("<#([0-9]+)>")),
    AudioChannel(Regex("<#([0-9]+)>")),
    MessageChannel(Regex("<#([0-9]+)>")),
    TextChannel(Regex("<#([0-9]+)>")),
    ThreadChannel(Regex("<#([0-9]+)>")),
    VoiceChannel(Regex("<#([0-9]+)>")),
    NewsChannel(Regex("<#([0-9]+)>")),
    StageChannel(Regex("<#([0-9]+)>")),
    Role(Regex("<@&([0-9]+)>")),
    Mentionable(Regex("<@[&!]?([0-9]+)>")),
    Long(Regex("[0-9]+")),
    Double(Regex("[0-9]+(?:.+[0-9]+)*")),
}
