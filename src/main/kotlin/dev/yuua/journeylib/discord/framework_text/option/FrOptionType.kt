package dev.yuua.journeylib.discord.framework_text.option

object FrOptionType {
    fun checkParseableTypes(option: Any) {

    }

    enum class MainType(vararg subtype: SubType) {
        STRING(SubType.String),
        BOOLEAN(SubType.Boolean),
        USER(SubType.User,SubType.Member),
        CHANNEL(
            SubType.GuildChannel,
            SubType.AudioChannel,
            SubType.MessageChannel,
            SubType.TextChannel,
            SubType.ThreadChannel,
            SubType.VoiceChannel,
            SubType.NewsChannel,
            SubType.StageChannel,
        ),
        ROLE(SubType.Role),
        MENTIONABLE(
            SubType.Mentionable,
            SubType.Role,
            SubType.Member
        ),
        INTEGER(SubType.Long),
        NUMBER(SubType.Long,SubType.Double),
    }

    enum class SubType {
        String,
        Boolean,
        User,
        Member,
        GuildChannel,
        AudioChannel,
        MessageChannel,
        TextChannel,
        ThreadChannel,
        VoiceChannel,
        NewsChannel,
        StageChannel,
        Role,
        Mentionable,
        Long,
        Double,
    }
}