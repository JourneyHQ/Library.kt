package dev.yuua.journeylib.discord.framework_text.option

import dev.yuua.journeylib.universal.LibString.removeQuote

/**
 * ### オプションを解析します
 * ex: "option1:"value2" option2='value2' value3"
 */
object FrOptionAnalysis {
    fun analyze(text: String): MutableList<FrOption> {
        val optionNameRegex = Regex("[\\w-]{1,32}")

        //value
        val optionRegex = Regex("[\\w-]+")
        //"value"
        val optionWithQuoteRegex = Regex("\"(?:[^\\\\\"]|\\\\\\\\|\\\\\")*\"")
        //option=value
        val namedOptionRegex = Regex("$optionNameRegex=$optionRegex")
        //option="value"
        val namedOptionWithQuoteRegex = Regex("$optionNameRegex=$optionWithQuoteRegex")

        val options = mutableListOf<FrOption>()

        for ((index, group) in Regex("($namedOptionWithQuoteRegex)|($namedOptionRegex)|($optionRegex)|($optionWithQuoteRegex)")
            .findAll(text).withIndex()) {
            val option = group.value
            //todo もうちょっと同じ処理をまとめたい
            val (name: String, value: String) = when {
                option.matches(namedOptionWithQuoteRegex) -> {
                    val splittedOption = option.split("=")
                    splittedOption[0] to splittedOption[1].removeQuote()
                }
                option.matches(namedOptionRegex) -> {
                    val splittedOption = option.split("=")
                    splittedOption[0] to splittedOption[1]
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
        return emptyList<FrOption>().toMutableList()
    }
}