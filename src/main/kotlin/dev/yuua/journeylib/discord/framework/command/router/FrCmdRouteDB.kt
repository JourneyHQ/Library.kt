package dev.yuua.journeylib.discord.framework.command.router

object FrCmdRouteDB {
    val record = mutableListOf<FrCmdRouteRecord>()

    fun find(cmd: String, subcmdGroup: String?, subcmd: String?): FrCmdRouteRecord {
        println(cmd)
        println(subcmdGroup)
        println(subcmd)
        record.forEach {
            println(it)
        }
        return record.filter {
            it.cmd == cmd && it.subcmdGroup == subcmdGroup && it.subcmd == subcmd
        }.also {
            if (it.size != 1)
                throw IllegalStateException("条件に一致するレコードが ${it.size} 個見つかりました！")
        }.first()
    }
}
