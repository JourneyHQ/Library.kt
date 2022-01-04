package dev.yuua.journeylib.config

import dev.yuua.journeylib.universal.LibFlow
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import kotlin.system.exitProcess

//todo Javadoc書く

object Config {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)
    lateinit var config: JSONObject
    lateinit var path: String

    val routes = HashMap<String, ConfigElement>()

    data class ConfigElement(val value: Any?, val type: OptionType)

    fun from(configPath: String = "./config.json", prototypePath: String = "./config.prototype.json") {
        checkFile(FileType.Config, configPath)
        checkFile(FileType.Prototype, prototypePath)

        for ((route, data) in ConfigAnalysis().analyze(config, null).routes
            .filter { (_, data) -> data.type != OptionType.Json }) {
            routes[route] = data
        }
        ConfigPrototype.init()

        var isValidConfig = true
        for ((route, option) in ConfigPrototype.routes) {
            if (option.isRequired && !routes.containsKey(route)) {
                isValidConfig = false
                libFlow.failure("$route は必須項目ですが、見つかりませんでした。")
                continue
            }

            if (!option.isRequired && !routes.containsKey(route)) {
                routes[route] = ConfigElement(option.defaultValue, option.type)
                continue
            }

            val data = routes[route]!!

            val validType = option.type
            val dataType = data.type
            if (validType != dataType) {
                isValidConfig = false
                libFlow.failure("$route は${validType.name}型ですが、${dataType.name}型が見つかりました。")
                continue
            }
        }
        if (!isValidConfig) {
            libFlow.failure("正しい設定ではなかったため、終了します。")
            exitProcess(-1)
        }
    }

    enum class FileType {
        Config, Prototype
    }

    private fun checkFile(fileType: FileType, filePath: String) {
        val configFile = File(filePath)
        val config: JSONObject
        if (configFile.exists()) {
            config = JSONObject(configFile.readText(Charset.defaultCharset()))
            libFlow.success("${fileType.name}ファイル ($filePath) を使用します！")
        } else {
            libFlow.failure("${fileType.name}ファイル ($filePath) が見つかりません！")
            exitProcess(-1)
        }
        when (fileType) {
            FileType.Config -> {
                this.path = filePath
                this.config = config
            }
            FileType.Prototype -> {
                ConfigPrototype.path = filePath
                ConfigPrototype.prototype = config
            }
        }
    }

    fun exists(route: String, optionType: OptionType = OptionType.Any): Boolean {
        return Route.exists(config, route, optionType)
    }

    enum class OptionType {
        Any, String, Boolean, Number, Json, List
    }

    operator fun get(route: String): String? {
        return routes[route]?.value as String?
    }

    private fun any(route: String): Any? {
        return routes[route]?.value
    }

    fun string(route: String): String? {
        return routes[route]?.value as String?
    }

    fun boolean(route: String): Boolean? {
        return routes[route]?.value.toString().toBooleanStrictOrNull()
    }

    fun number(route: String): Number? {
        return routes[route]?.value.toString().toIntOrNull()
    }

    fun list(route: String): List<*>? {
        return routes[route]?.value as List<*>?
    }
}