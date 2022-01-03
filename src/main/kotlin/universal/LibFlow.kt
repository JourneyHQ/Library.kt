package universal

import universal.LibTermColor.RESET

/**
 * ### ログを統一された書式で出力します。
 * @param globalName デフォルトで使用される出力名
 */
class LibFlow(private val globalName: String? = null) {
    enum class Type(val symbol: String, val color: String) {
        HEADER(":", LibTermColor.BLUE_BRIGHT),
        INFO("i", LibTermColor.PURPLE_BRIGHT),
        TASK(">", LibTermColor.YELLOW_BRIGHT),
        SUCCESS("+", LibTermColor.GREEN_BRIGHT),
        FAILURE("#", LibTermColor.RED_BRIGHT)
    }

    /**
     * ### 指定された情報を出力形式にフォーマットして返します。
     * @param name 名前
     * @param type 出力タイプ
     * @param context 内容
     */
    private fun getFormatted(name: String, type: Type, context: String?): String {
        return "|${type.color + type.symbol + RESET}|[${LibTermColor.BLACK_BRIGHT + name + RESET}] $context"
    }

    /**
     * ### HEADERを出力します。
     * @param name 名前
     */
    fun header(name: String): LibFlow {
        println(getFormatted(name, Type.HEADER, "==="))
        return this
    }

    /**
     * ### INFOを出力します。
     * @param name 名前
     * @param context 内容
     */
    fun info(name: String? = null, context: String?): LibFlow {
        println(getFormatted(name ?: globalName ?: "INFO", Type.INFO, context))
        return this
    }

    /**
     * ### INFOを出力します。
     * 名前を省略してグローバル名を使用します。
     * @param context 内容
     */
    fun info(context: String?): LibFlow {
        println(getFormatted(globalName ?: "INFO", Type.INFO, context))
        return this
    }

    /**
     * ### TASKを出力します。
     * @param name 名前
     * @param context 内容
     */
    fun task(name: String? = null, context: String?): LibFlow {
        println(getFormatted(name ?: globalName ?: "TASK", Type.TASK, context))
        return this
    }

    /**
     * ### TASKを出力します。
     * 名前を省略してグローバル名を使用します。
     * @param context 内容
     */
    fun task(context: String?): LibFlow {
        println(getFormatted(globalName ?: "TASK", Type.TASK, context))
        return this
    }

    /**
     * ### SUCCESSを出力します。
     * @param name 名前
     * @param context 内容
     */
    fun success(name: String? = null, context: String?): LibFlow {
        println(getFormatted(name ?: globalName ?: "SUCCESS", Type.SUCCESS, context))
        return this
    }

    /**
     * ### SUCCESSを出力します。
     * 名前を省略してグローバル名を使用します。
     * @param context 内容
     */
    fun success(context: String?): LibFlow {
        println(getFormatted(globalName ?: "SUCCESS", Type.SUCCESS, context))
        return this
    }

    /**
     * ### FAILUREを出力します。
     * @param name 名前
     * @param context 内容
     */
    fun failure(name: String? = null, context: String?): LibFlow {
        println(getFormatted(name ?: globalName ?: "FAILURE", Type.FAILURE, context))
        return this
    }

    /**
     * ### FAILUREを出力します。
     * 名前を省略してグローバル名を使用します。
     * @param context 内容
     */
    fun failure(context: String?): LibFlow {
        println(getFormatted(globalName ?: "FAILURE", Type.FAILURE, context))
        return this
    }
}