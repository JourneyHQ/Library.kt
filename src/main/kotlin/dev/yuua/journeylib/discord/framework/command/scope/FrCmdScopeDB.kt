package dev.yuua.journeylib.discord.framework.command.scope

import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmdStruct
import dev.yuua.journeylib.discord.framework.command.router.FrCmdRouteRecord
import org.reflections.Reflections

object FrCmdScopeDB {
    val record = hashMapOf<MutableList<FrCmdRouteRecord>, FrCmdScope>()

    fun add(scopedPackage: String, scope: FrCmdScope) {
        val classes =
            Reflections("${FrameworkManager.commandPackage}.$scopedPackage")
                .getSubTypesOf(FrCmdStruct::class.java)
        val records = mutableListOf<FrCmdRouteRecord>()
        val cmdSubstrates = classes.map { it.getConstructor().newInstance().cmd().cmd.routes }

        for (cmdSubstrate in cmdSubstrates) records.addAll(cmdSubstrate)

        record[records] = scope
    }

    fun find(routeRecord: FrCmdRouteRecord): MutableList<FrCmdScope> {
        return record.filter { it.key.contains(routeRecord) }.values.toMutableList()
    }
}
