package dev.yuua.journeylib.config

import dev.yuua.journeylib.universal.LibFlow
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import kotlin.system.exitProcess

object Config {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)
    lateinit var config: JSONObject
    lateinit var path: String

    val routes = HashMap<String, ConfigElement>()

    /**
     * Data class for storing config the value and type.
     * @param value Value of config element
     * @param type Type of config element
     */
    data class ConfigElement(val value: Any?, val type: OptionType)

    /**
     * Load config and config struct.
     * @param configPath Path to config file (default: ./config.json)
     * @param structPath Path to config struct file (default: ./config.struct.json)
     */
    fun from(configPath: String = "./config.json", structPath: String = "./config.struct.json") {
        checkFile(FileType.Config, configPath)
        checkFile(FileType.Struct, structPath)

        for ((route, data) in ConfigAnalysis().analyze(config, null).routes
            .filter { (_, data) -> data.type != OptionType.Json }) {
            routes[route] = data
        }
        ConfigStruct.init()

        var isValidConfig = true
        for ((route, option) in ConfigStruct.routes) {
            if (option.isRequired && !routes.containsKey(route)) {
                isValidConfig = false
                libFlow.failure("$route is a required option, but not found.")
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
                libFlow.failure("$route is a ${validType.name} type, but a ${dataType.name} type was found.")
                continue
            }
        }
        if (!isValidConfig) {
            libFlow.failure("Exit because the config file was invalid!")
            exitProcess(-1)
        }
    }

    /**
     * The enum of config file type. (config file or struct file)
     */
    enum class FileType {
        Config, Struct
    }

    /**
     * Checks to see whether the file exists and, if so, loads it.
     * @param fileType Type of file
     * @param filePath Path ti file
     */
    private fun checkFile(fileType: FileType, filePath: String) {
        val configFile = File(filePath)
        val config: JSONObject
        if (configFile.exists()) {
            config = JSONObject(configFile.readText(Charset.defaultCharset()))
            libFlow.success("Use File:${fileType.name} ($filePath)")
        } else {
            libFlow.failure("File:${fileType.name} ($filePath) not found!")
            exitProcess(-1)
        }
        when (fileType) {
            FileType.Config -> {
                this.path = filePath
                this.config = config
            }
            FileType.Struct -> {
                ConfigStruct.path = filePath
                ConfigStruct.struct = config
            }
        }
    }

    /**
     * Checks to see whether the config element exists.
     * @param route Route to config element
     * @param optionType Type of config element
     */
    fun exists(route: String, optionType: OptionType = OptionType.Any): Boolean {
        return Route.exists(config, route, optionType)
    }

    /**
     * Type of config value.
     */
    enum class OptionType {
        Any, String, Boolean, Number, Json, List
    }

    /**
     * Get string value from the config.
     * @param route Route to config element
     */
    operator fun get(route: String): String? {
        return routes[route]?.value as String?
    }

    /**
     * Get the value as any from the config.
     * @param route Route to config element
     */
    private fun any(route: String): Any? {
        return routes[route]?.value
    }

    /**
     * Get the value as string from the config.
     * @param route Route to config element
     */
    fun string(route: String): String? {
        return routes[route]?.value as String?
    }

    /**
     * Get the value as boolean from the config.
     * @param route Route to config element
     */
    fun boolean(route: String): Boolean? {
        return routes[route]?.value.toString().toBooleanStrictOrNull()
    }

    /**
     * Get the value as number from the config.
     * @param route Route to config element
     */
    fun number(route: String): Number? {
        return routes[route]?.value.toString().toIntOrNull()
    }

    /**
     * Get the value as list from the config.
     * @param route Route to config element
     */
    fun list(route: String): List<*>? {
        return routes[route]?.value as List<*>?
    }
}
