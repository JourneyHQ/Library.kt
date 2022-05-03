package dev.yuua.journeylib.discord.framework.function.scope

import dev.yuua.journeylib.discord.framework.Framework
import dev.yuua.journeylib.discord.framework.function.command.builder.structure.FrCommand
import dev.yuua.journeylib.discord.framework.function.command.builder.structure.FrCommandStruct
import dev.yuua.journeylib.discord.framework.function.command.router.FrCmdRouteRecord
import dev.yuua.journeylib.universal.LibFlow
import org.reflections.Reflections

class FrCmdScopeDB(private val framework: Framework) {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    val record = hashMapOf<String, FrCmdScope>()

    init {
        val scopeClasses = Reflections(framework.commandPackage)
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
            Reflections("${framework.commandPackage}.$scopedPackage")
                .getSubTypesOf(FrCommandStruct::class.java)

        val frCmds = classes.map { it.getConstructor().newInstance().cmd().cmd }

        for (frCmd in frCmds) record[frCmd.name] = scope

        return this
    }

    fun find(frCommand: FrCommand): FrCmdScope? {
        val list = record.filterKeys { it == frCommand.name }.values.toList()
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
