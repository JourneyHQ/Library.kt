package dev.yuua.journeylib.config

import dev.yuua.journeylib.universal.LibFlow
import org.json.JSONObject

object ConfigPrototype {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)
    lateinit var prototype: JSONObject
    lateinit var path: String

    val routes = HashMap<String, ConfigOption>()

    data class ConfigOption(
        val type: Config.OptionType,
        val isRequired: Boolean,
        val defaultValue: Any?
    )

    fun init() {
        for ((route, data) in ConfigAnalysis().analyze(prototype, null)
            .routes.filter { (_, data) -> data.type == Config.OptionType.String })
            routes[route] = analyzeSyntax(data.value as String)
    }

    private fun analyzeSyntax(string: String): ConfigOption {
        val options = string.split(":")
        var optionType: Config.OptionType = Config.OptionType.Any
        var isRequired = true
        var defaultValue: Any? = null
        for ((index, option) in options.withIndex()) {
            when (index) {
                0 -> {
                    optionType = try {
                        Config.OptionType.valueOf(
                            option.substring(0, 1).uppercase()
                                    + option.substring(1).lowercase()
                        )
                    } catch (_: IllegalArgumentException) {
                        libFlow.failure("$string 構文が不正です。指定された型が見つかりません。")
                        continue
                    }
                }
                1 -> {
                    isRequired = when (option) {
                        "required", "true" -> true
                        "optional", "false" -> false
                        else -> true
                    }
                }
                2 -> {
                    defaultValue = option
                }
            }
        }
        return ConfigOption(optionType, isRequired, defaultValue)
    }
}