package dev.yuua.journeylib.discord.framework.scope

import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.universal.LibClassFinder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

object CmdScopeManager {
    interface CmdScope {
        val guilds: Array<String>

        fun rule(rule: (event: SlashCommandEvent) -> Boolean): Boolean = true
    }

    val record: HashMap<String, CmdScope> = HashMap()

    fun init() {
        for (scopeClass in LibClassFinder().findClasses(FrameworkManager.commandPackage)) {
            if (scopeClass.enclosingClass != null
                || scopeClass.name.contains("$") ||
                !scopeClass.simpleName.equals("Scope")
            ) continue
            val scope: CmdScope = scopeClass.getConstructor().newInstance() as CmdScope
            record[scope.javaClass.packageName] = scope
        }
    }
}