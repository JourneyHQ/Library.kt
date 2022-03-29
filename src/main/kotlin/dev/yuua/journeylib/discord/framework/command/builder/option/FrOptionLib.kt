package dev.yuua.journeylib.discord.framework.command.builder.option

import dev.yuua.journeylib.universal.LibString.removeQuote
import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * ### オプションを解析します
 * ex: option1=value2 option2="value 2" value3 "value 4"
 */
object FrOptionLib {
    enum class OptionAnalysisResultType(val code: String) {
        RequiredNotFound("Required Options Not Found"),
        MixedOption("Mixed Option Syntax"),
        Success("")
    }

    data class OptionAnalysisResult(
        val options: MutableList<FrOptionIndex>,
        val message: String,
        val type: OptionAnalysisResultType
    )

    fun analyze(text: String, frOptions: MutableList<OptionData>): OptionAnalysisResult {
        val optionNameRegex = Regex("[\\w-]{1,32}")

        //value
        val optionRegex = Regex(".+")
        //"value"
        val optionWithQuoteRegex = Regex("\"(?:[^\\\\\"]|\\\\\\\\|\\\\\")*\"")
        //option=value
        val namedOptionRegex = Regex("($optionNameRegex)=($optionRegex)")
        //option="value"
        val namedOptionWithQuoteRegex = Regex("($optionNameRegex)=($optionWithQuoteRegex)")


        val allRegex = Regex("($namedOptionWithQuoteRegex)|($namedOptionRegex)|($optionWithQuoteRegex)|($optionRegex)")

        data class NamedOption(val index: Number, val name: String, val value: Any)

        val namedOptions = mutableListOf<NamedOption>()
        val indexedOptions = mutableListOf<NamedOption>()

        for ((index, group) in allRegex.findAll(text).withIndex()) {
            val optionText = group.value
            when {
                optionText.matches(namedOptionWithQuoteRegex) || optionText.matches(namedOptionRegex) -> {
                    val splitOption = optionText.split("=")
                    namedOptions.add(NamedOption(index, splitOption[0], splitOption[1].removeQuote()))
                }
                optionText.matches(optionWithQuoteRegex) || optionText.matches(optionRegex) -> {
                    index.toString() to optionText.removeQuote()
                    indexedOptions.add(NamedOption(index, index.toString(), optionText.removeQuote()))
                }
                //通常発生しない...?
                else -> throw IllegalArgumentException("$index does not match any of the types!")
            }
        }

        val options = mutableListOf<FrOptionIndex>()
        val frOptionsClone = mutableListOf(*frOptions.toTypedArray())
        when {
            //namedOptions
            namedOptions.isNotEmpty() && indexedOptions.isEmpty() -> {
                for (namedOption in namedOptions) {
                    val frOption = frOptionsClone.first { it.name == namedOption.name }
                    options.add(
                        FrOptionIndex().fromData(
                            frOption.name,
                            namedOption.value,
                            frOption.type
                        )
                    )
                    frOptionsClone.remove(frOption)
                }
            }
            //indexedOptions
            namedOptions.isEmpty() && indexedOptions.isNotEmpty() -> {

                for ((index, indexedOption) in indexedOptions.withIndex()) {
                    val frOption = frOptionsClone[index]
                    options.add(
                        FrOptionIndex().fromData(
                            frOption.name,
                            indexedOption.value,
                            frOption.type
                        )
                    )
                    frOptionsClone.remove(frOption)
                }
            }
            namedOptions.isNotEmpty() && indexedOptions.isNotEmpty() ->
                return OptionAnalysisResult(
                    mutableListOf(),
                    "Mixed option syntax found.\nPlease use consistent syntax.",
                    OptionAnalysisResultType.MixedOption
                )
        }

        val requiredOptions = frOptionsClone.filter { it.isRequired }
        val requiredOptionsString = requiredOptions.joinToString(",") { it.name }

        if (requiredOptions.isNotEmpty())
            return OptionAnalysisResult(
                mutableListOf(),
                "Required option: $requiredOptionsString not found.\nPlease refer to help.",
                OptionAnalysisResultType.RequiredNotFound
            )

        return OptionAnalysisResult(options, "", OptionAnalysisResultType.Success)
    }
}
