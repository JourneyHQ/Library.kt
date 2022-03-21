package dev.yuua.journeylib.discord.framework.command.scope

import dev.yuua.journeylib.discord.framework.command.builder.FrCmdBuilderExtention.toFrCmdRouteRecord
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmdSubstrate
import dev.yuua.journeylib.discord.framework.command.router.FrCmdRouteRecord
import org.reflections.Reflections

object FrCmdScopeDB {
    val record = hashMapOf<MutableList<FrCmdRouteRecord>, FrCmdScope>()

    fun add(scopedPackage: String, scope: FrCmdScope) {
        val classes = Reflections(scopedPackage).getSubTypesOf(FrCmdSubstrate::class.java)
        val records = mutableListOf<FrCmdRouteRecord>()
        val cmdSubstrates = classes.map { it.getConstructor().newInstance().toFrCmdRouteRecord() }

        for (cmdSubstrate in cmdSubstrates) records.addAll(cmdSubstrate)

        record[records] = scope
    }

    fun find(routeRecord: FrCmdRouteRecord): MutableList<FrCmdScope> {
        return record.filter { it.key.contains(routeRecord) }.values.toMutableList()
    }
}
