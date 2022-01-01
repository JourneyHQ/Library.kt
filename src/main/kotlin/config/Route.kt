package config

import org.json.JSONException
import org.json.JSONObject

object Route {
    fun exists(json: JSONObject, route: String, optionType: Config.OptionType): Boolean {
        return analysis(json, route, optionType).isValid
    }

    fun analysis(json: JSONObject, route: String, optionType: Config.OptionType): AnalysisResult {
        val elements = route.split(".")
        val objects = elements.dropLast(1)
        val last = elements.last()
        var current = json
        val default = ConfigPrototype.routes[route]?.defaultValue

        for (element in objects) {
            if (!current.has(element))
                return AnalysisResult(false, default, Config.OptionType.Any)
            current = current.getJSONObject(element)
        }

        return if (current.has(last)) {
            val value = try {
                when (optionType) {
                    Config.OptionType.Any -> current.get(last)
                    Config.OptionType.String -> current.getString(last)
                    Config.OptionType.Boolean -> current.getBoolean(last)
                    Config.OptionType.Number -> current.getNumber(last)
                    Config.OptionType.Json -> current.getJSONObject(last)
                    Config.OptionType.List -> current.getJSONArray(last).toList()
                }
            } catch (e: JSONException) {
                default
            }
            AnalysisResult(true, value, optionType)
        } else {
            AnalysisResult(false, default, optionType)
        }
    }

    data class AnalysisResult(val isValid: Boolean, val value: Any?, val optionType: Config.OptionType)
}