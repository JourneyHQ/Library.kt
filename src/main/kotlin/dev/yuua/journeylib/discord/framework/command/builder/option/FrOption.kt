package dev.yuua.journeylib.discord.framework.command.builder.option

import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionLib.toJDAOptionType
import dev.yuua.journeylib.discord.framework.command.builder.option.type.MainType
import dev.yuua.journeylib.discord.framework.command.builder.option.type.SubType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class FrOption(
    val mainType: MainType,
    val name: String,
    val details: String,
    val required: Boolean = true,
    val autoComplete: Boolean = true
) {
    val jdaOption = OptionData(mainType.toJDAOptionType(), name, details, required, autoComplete)

    var subType = mainType.subtype

    fun acceptSubType(vararg subType: SubType): FrOption {
        for (subTypeIndex in subType) {
            if (!mainType.subtype.contains(subTypeIndex))
                throw TypeCastException("${mainType.name} に ${subTypeIndex.name} は含まれていません！")
        }

        this.subType = subType
        return this
    }
}
