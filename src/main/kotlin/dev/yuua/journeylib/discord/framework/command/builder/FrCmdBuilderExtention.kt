package dev.yuua.journeylib.discord.framework.command.builder

import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmdSubstrate
import dev.yuua.journeylib.discord.framework.command.router.FrCmdRouteRecord

object FrCmdBuilderExtention {
    fun FrCmdSubstrate.toFrCmdRouteRecord(): MutableList<FrCmdRouteRecord> {
        return this.cmd().cmd.routes
    }
}
