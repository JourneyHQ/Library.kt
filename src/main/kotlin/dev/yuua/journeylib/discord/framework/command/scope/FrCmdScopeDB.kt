package dev.yuua.journeylib.discord.framework.command.scope

import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmd
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmdStruct
import dev.yuua.journeylib.discord.framework.command.router.FrCmdRouteRecord
import dev.yuua.journeylib.universal.LibFlow
import org.reflections.Reflections

object FrCmdScopeDB {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    val record = hashMapOf<String, FrCmdScope>()

    fun init() {
        val scopeClasses = Reflections(FrameworkManager.commandPackage)
            .getSubTypesOf(FrCmdScopeStruct::class.java)

        for (scopeClass in scopeClasses) {
            val scopeClassInst = scopeClass.getConstructor().newInstance()
            scopeClassInst.scope().forEach { (packageString, scope) ->
                add(packageString, scope)
                libFlow.success("Successfully applied scope to $packageString")
            }
        }
    }

    private fun add(scopedPackage: String, scope: FrCmdScope): FrCmdScopeDB {
        val classes =
            Reflections("${FrameworkManager.commandPackage}.$scopedPackage")
                .getSubTypesOf(FrCmdStruct::class.java)

        val frCmds = classes.map { it.getConstructor().newInstance().cmd().cmd }

        for (frCmd in frCmds) record[frCmd.name] = scope

        return this
    }

    fun find(frCmd: FrCmd): FrCmdScope? {
        val list = record.filterKeys { it == frCmd.name }.values.toList()
        return if (list.size == 1)
            list[0]
        else null
    }

    fun find(frCmdRouteRecord: FrCmdRouteRecord): FrCmdScope? {
        val list = record.filterKeys { frCmdRouteRecord.cmd.allNames().contains(it) }.values.toList()
        return if (list.size == 1)
            list[0]
        else null
    }
}
