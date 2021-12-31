package dev.yuua.journeylib

import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import kotlin.system.exitProcess

object LibConfig {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)
    lateinit var path: String
    lateinit var config: JSONObject

    /**
     * ### Configファイルのパスを指定します。
     * @param filePath Configのファイルパス
     */
    fun from(filePath: String): LibConfig {
        this.path = filePath
        val file = File(filePath)

        if (file.exists()) {
            config = JSONObject(file.readText(Charset.defaultCharset()))
            libFlow.success("設定ファイル ($filePath) を使用します！")
        } else {
            LibFlow("LibConfig")
                .failure("指定された設定ファイル ($filePath) が見つかりません！")
                .failure("終了します...")
            exitProcess(-1)
        }

        return this
    }

    /**
     * ### ConfigからString形式で値を取得します。
     * - 例: token.discord -> tokenオブジェクト内のdiscord(String)
     * @param path String値へのパス
     */
    operator fun get(path: String): String? {
        val pathAnalysisData = pathAnalysis(path)
        return pathAnalysisData.lastJSONObject?.getString(pathAnalysisData.lastKey)
    }

    /**
     * ### ConfigからInteger形式で値を取得します。
     * @param path Integer値へのパス
     */
    fun getInt(path: String): Int? {
        val pathAnalysisData = pathAnalysis(this.path)
        return pathAnalysisData.lastJSONObject?.getInt(pathAnalysisData.lastKey)
    }

    /**
     * ### ConfigからObject形式で値を取得します。
     * @param path Objectへのパス
     */
    fun getObject(path: String): JSONObject? {
        val pathAnalysisData = pathAnalysis(this.path)
        return pathAnalysisData.lastJSONObject?.getJSONObject(pathAnalysisData.lastKey)
    }

    /**
     * ### ConfigからArray形式で値を取得します。
     * @param path Arrayへのパス
     */
    fun getArray(path: String): List<Any>? {
        val pathAnalysisData = pathAnalysis(this.path)
        return pathAnalysisData.lastJSONObject?.getJSONArray(pathAnalysisData.lastKey)?.toList()
    }

    /**
     * ### Pathを解析して最後のオブジェクトとキーを返します。
     * @param path 何かへのパス
     */
    private fun pathAnalysis(path: String): PathAnalysisData {
        var current = config

        if (!path.contains(".")) return PathAnalysisData(current, path)

        val pathArray = path.split(".")
        val pathDirectory = pathArray.dropLast(1)
        val pathLastKey = pathArray.last()

        for (directory in pathDirectory) {
            when (val currentDirectory = current[directory]) {
                is JSONObject -> current = currentDirectory
                else -> return PathAnalysisData(null, pathLastKey)
            }
        }
        return PathAnalysisData(current, pathLastKey)
    }

    private data class PathAnalysisData(val lastJSONObject: JSONObject?, val lastKey: String)
}