package dev.yuua.journeylib.discord.framework.command.builder.option

import dev.yuua.journeylib.discord.framework.command.builder.option.type.MainType
import dev.yuua.journeylib.universal.LibString.removeQuote
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * ### オプションを解析します
 * ex: option1:"value2" option2='value2' value3
 */
object FrOptionLib {
    fun OptionType.toFrOptionType(): MainType {
        return MainType.valueOf(this.name)
    }

    fun MainType.toJDAOptionType(): OptionType {
        return OptionType.valueOf(this.name)
    }

    fun FrOption.toJDAOptionData(): OptionData {
        return OptionData(
            this.mainType.toJDAOptionType(),
            this.name,
            this.details,
            this.required,
            this.autoComplete
        )
    }

    fun analyze(text: String): MutableList<FrOptionIndex> {
        val optionNameRegex = Regex("[\\w-]{1,32}")

        //value
        val optionRegex = Regex("[\\w-]+")
        //"value"
        val optionWithQuoteRegex = Regex("\"(?:[^\\\\\"]|\\\\\\\\|\\\\\")*\"")
        //option=value
        val namedOptionRegex = Regex("($optionNameRegex)=($optionRegex)")
        //option="value"
        val namedOptionWithQuoteRegex = Regex("($optionNameRegex)=($optionWithQuoteRegex)")

        val options = mutableListOf<FrOptionIndex>()

        for ((index, group) in Regex("($namedOptionWithQuoteRegex)|($namedOptionRegex)|($optionRegex)|($optionWithQuoteRegex)")
            .findAll(text).withIndex()) {
            val option = group.value
            //todo もうちょっと同じ処理をまとめたい
            val (name: String, value: String) = when {
                option.matches(namedOptionWithQuoteRegex) -> {
                    val splitOption = option.split("=")
                    splitOption[0] to splitOption[1].removeQuote()
                }
                option.matches(namedOptionRegex) -> {
                    val splitOption = option.split("=")
                    splitOption[0] to splitOption[1]
                }
                option.matches(optionWithQuoteRegex) -> {
                    index.toString() to option.removeQuote()
                }
                option.matches(optionRegex) -> {
                    index.toString() to option
                }
                else -> {
                    continue
                }
            }
            //FrOption().fromData(name,null,value)
        }
        return emptyList<FrOptionIndex>().toMutableList()
    }
}
