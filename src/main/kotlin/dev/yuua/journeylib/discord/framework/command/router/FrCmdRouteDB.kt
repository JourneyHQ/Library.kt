package dev.yuua.journeylib.discord.framework.command.router

object FrCmdRouteDB {
    val record = mutableListOf<FrCmdRouteRecord>()

    fun find(cmd: String, subcmdGroup: String?, subcmd: String?): FrCmdRouteRecord {
        return record.filter {
            it.cmd.allNames().contains(cmd) &&
                    it.subcmdGroup.allNames().contains(subcmdGroup) &&
                    it.subcmd.allNames().contains(subcmd)
        }.also {
            if (it.size != 1)
                throw IllegalStateException("${it.size} record(s) were found that match the criteria!")
        }.first()
    }

    fun findStruct(cmd: String): FrCmdTypeEnum {
        return record.first { it.cmd.allNames().contains(cmd) }.struct
    }
}
