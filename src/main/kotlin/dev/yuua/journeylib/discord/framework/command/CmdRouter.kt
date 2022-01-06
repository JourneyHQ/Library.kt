package dev.yuua.journeylib.discord.framework.command

object CmdRouter {
    /**
     * ### コマンドのデータが全て追加されます。
     */
    val RouteData = HashMap<CmdIdentifier, CmdIndex>()

    /**
     * - CmdIdentifier: Commandを名前で判別するためのレコードです。<br>
     * - CmdIndex: Commandを実行するためのFunction / 判定用のPermission を入れるレコードです。
     */
    data class CmdIdentifier(val command: String, val subcommandGroup: String?, val subcommand: String?)
    data class CmdIndex(val cmdFunction: CmdFunction, val permission: List<CmdPermission>)

    /**
     * ### 解析中のFunctionを一時的に置いておくリストです。
     * 一つのコマンドの解析が終わるごとにクリアされます。
     */
    val FunctionTemp = ArrayList<CmdFunction>()

    data class NameToPermission(val name: String, val permission: CmdPermission)

    /**
     * ### 解析中のPermissionを一時的に置いておくリストです。
     * 一つのコマンドの解析が終わるごとにクリアされます。
     */
    var CmdPermissionTemp: CmdPermission? = null
    val SubcmdGroupPermissionTemp = ArrayList<NameToPermission>()
    val SubcmdPermissionTemp = ArrayList<NameToPermission>()
    fun permTempClearAll() {
        CmdPermissionTemp = null
        SubcmdGroupPermissionTemp.clear()
        SubcmdPermissionTemp.clear()
    }
}