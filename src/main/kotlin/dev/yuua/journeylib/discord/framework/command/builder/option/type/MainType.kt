package dev.yuua.journeylib.discord.framework.command.builder.option.type

enum class MainType(val regex: Regex?, vararg val subtype: SubType) {
    STRING(null, SubType.String),
    BOOLEAN(Regex("(true|false|yes|no)"), SubType.Boolean),
    USER(Regex("<@!?([0-9]+)>"), SubType.User, SubType.Member),
    CHANNEL(
        Regex("<#([0-9]+)>"),
        SubType.GuildChannel,
        SubType.AudioChannel,
        SubType.MessageChannel,
        SubType.TextChannel,
        SubType.ThreadChannel,
        SubType.VoiceChannel,
        SubType.NewsChannel,
        SubType.StageChannel,
    ),
    ROLE(Regex("<@&([0-9]+)>"), SubType.Role),
    MENTIONABLE(
        Regex("<@[&!]?([0-9]+)>"),
        SubType.Mentionable,
        SubType.Role,
        SubType.Member
    ),
    INTEGER(Regex("[0-9]+"), SubType.Long),
    NUMBER(Regex("[0-9]+"), SubType.Long, SubType.Double),
    ATTACHMENT(null);
}
