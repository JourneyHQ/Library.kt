package dev.yuua.librarykt.qnortz.functions.command.builder.option

import dev.yuua.librarykt.universal.LibString.trimQuote
import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * Result of [analyzeOptions]
 * @param options detected options.
 * @param message error message which displayed if there are invalid syntax.
 */
data class OptionAnalysisResult(
    val options: MutableList<UnifiedOption> = mutableListOf(),
    val message: String? = null
)

/**
 * Analyzes options of TextCommand.
 * @param text option part of command string.
 * @param optionDataList list of [OptionData] for determining if option syntax is correct.
 */
fun analyzeOptions(text: String, optionDataList: List<OptionData>): OptionAnalysisResult {
    val optionNameRegex = Regex("[\\w-]{1,32}")

    val optionRegex = Regex("[^ ]+") // value
    val optionWithQuoteRegex = Regex("\"(?:[^\\\\\"]|\\\\\\\\|\\\\\")*\"") // "value"
    val namedOptionRegex = Regex("($optionNameRegex)=($optionRegex)") // option=value
    val namedOptionWithQuoteRegex = Regex("($optionNameRegex)=($optionWithQuoteRegex)") // option="value"

    val allRegex = Regex("($namedOptionWithQuoteRegex)|($namedOptionRegex)|($optionWithQuoteRegex)|($optionRegex)")

    data class NamedOption(val index: Number, val name: String, val value: Any)

    val namedOptions = mutableListOf<NamedOption>()
    val indexedOptions = mutableListOf<NamedOption>()

    for ((index, group) in allRegex.findAll(text).withIndex()) {
        val optionText = group.value
        when {
            // option=value or option="value"
            optionText.matches(namedOptionWithQuoteRegex) || optionText.matches(namedOptionRegex) -> {
                val splitOption = optionText.split("=")
                namedOptions += NamedOption(index, splitOption[0], splitOption[1].trimQuote())
            }
            // value or "value"
            optionText.matches(optionWithQuoteRegex) || optionText.matches(optionRegex) -> {
                index.toString() to optionText.trimQuote()
                indexedOptions += NamedOption(index, index.toString(), optionText.trimQuote())
            }
            // never happen...? maybe.
            else -> throw IllegalArgumentException("$index does not match any of the types!")
        }
    }

    val options = mutableListOf<UnifiedOption>()
    val optionDataListClone = mutableListOf(*optionDataList.toTypedArray())

    when {
        // namedOptions only.
        namedOptions.isNotEmpty() && indexedOptions.isEmpty() -> {
            for (namedOption in namedOptions) {
                // if there is no option with such a name, skip it.
                val option = optionDataListClone.firstOrNull { it.name == namedOption.name } ?: continue
                options.add(
                    UnifiedOption(
                        option.name,
                        namedOption.value,
                        option.type
                    )
                )
                optionDataListClone.remove(option)
            }
        }
        // indexedOptions only.
        namedOptions.isEmpty() && indexedOptions.isNotEmpty() -> {
            for ((index, indexedOption) in indexedOptions.withIndex()) {
                val option = optionDataList[index]
                options.add(
                    UnifiedOption(
                        option.name,
                        indexedOption.value,
                        option.type
                    )
                )
                optionDataListClone.remove(option)
            }
        }

        namedOptions.isEmpty() && indexedOptions.isEmpty() -> {
            /* no-op */
        }

        else ->
            return OptionAnalysisResult(message = "Mixed option syntax found.\nPlease use consistent syntax.")
    }

    // required options that is not removed above.
    val requiredOptions = optionDataListClone.filter { it.isRequired }
    val requiredOptionsString = requiredOptions.joinToString(",") { it.name }

    if (requiredOptions.isNotEmpty()) {
        return OptionAnalysisResult(
            message = "Required option: $requiredOptionsString not found.\nPlease refer to help."
        )
    }

    for (optionData in optionDataList) {
        val option = options.firstOrNull { it.name == optionData.name } ?: continue
        if (!matchType(optionData.type, option.value))
            return OptionAnalysisResult(
                message = "Invalid option type found: ${optionData.name}(${option.value}).\n${optionData.type} expected."
            )
    }

    return OptionAnalysisResult(options)
}
