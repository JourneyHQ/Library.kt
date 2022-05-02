package dev.yuua.journeylib.discord.framework.function.scope

import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.discord.framework.function.command.builder.structure.FrCmd
import dev.yuua.journeylib.discord.framework.function.command.builder.structure.FrCommandStruct
import dev.yuua.journeylib.discord.framework.function.command.router.FrCmdRouteRecord
import dev.yuua.journeylib.universal.LibFlow
import org.reflections.Reflections

class FrCmdScopeDB(private val frameworkManager: FrameworkManager) {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    val record = hashMapOf<String, FrCmdScope>()

    init {
        val scopeClasses = Reflections(frameworkManager.commandPackage)
            .getSubTypesOf(FrScopeStruct::class.java)

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
            Reflections("${frameworkManager.commandPackage}.$scopedPackage")
                .getSubTypesOf(FrCommandStruct::class.java)

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
