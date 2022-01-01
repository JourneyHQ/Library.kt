package config

import config.Config.ConfigElement
import org.json.JSONArray
import org.json.JSONObject

class ConfigAnalysis {
    val routes = HashMap<String, ConfigElement>()

    fun analyze(json: JSONObject, current: String?): ConfigAnalysis {
        for (key in json.keys()) {
            val route = if (current == null) key else "$current.$key"
            when (val element = json[key]) {
                is JSONObject -> {
                    routes[route] = ConfigElement(element, Config.OptionType.Json)
                    analyze(element, route)
                }
                is JSONArray -> {
                    routes[route] = ConfigElement(element.toList(), Config.OptionType.List)
                    for ((index, listElement) in element.withIndex()) {
                        val routeToIndex = "$route.$index"
                        when (listElement) {
                            is JSONObject -> {
                                routes[routeToIndex] = ConfigElement(listElement, Config.OptionType.Json)
                                analyze(listElement, routeToIndex)
                            }
                            is String -> routes[routeToIndex] =
                                ConfigElement(listElement, Config.OptionType.String)
                            is Boolean -> routes[routeToIndex] =
                                ConfigElement(listElement, Config.OptionType.Boolean)
                            is Number -> routes[routeToIndex] =
                                ConfigElement(listElement, Config.OptionType.Number)
                            else -> routes[routeToIndex] =
                                ConfigElement(listElement, Config.OptionType.Any)
                        }
                    }
                }
                is String -> routes[route] = ConfigElement(element, Config.OptionType.String)
                is Boolean -> routes[route] = ConfigElement(element, Config.OptionType.Boolean)
                is Number -> routes[route] = ConfigElement(element, Config.OptionType.Number)
                else -> routes[route] = ConfigElement(element, Config.OptionType.Any)
            }
        }
        return this
    }
}