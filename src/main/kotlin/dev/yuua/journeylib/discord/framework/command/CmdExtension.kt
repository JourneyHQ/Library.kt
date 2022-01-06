package dev.yuua.journeylib.discord.framework.command

import dev.yuua.journeylib.discord.framework.command.CmdExtension.CmdType.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import java.util.*

/**
 * ### CommandDataなどのクラスを拡張し、Function,権限を結びつけられるようにします。
 */
object CmdExtension {
    /**
     * ### CommandDataにFunctionを結びつけます。
     * SubcommandDataのFunctionと重複させることは出来ません。
     * @param function 設定するCmdFunction
     * @return CommandData
     */
    fun CommandData.setCmdFunction(function: CmdFunction): CommandData {
        CmdRouter.FunctionTemp.add(function)
        return this
    }

    /**
     * ### SubcommandDataにFunctionを結びつけます。
     * CommandDataのFunctionと重複させることは出来ません。
     * @param function 設定するCmdFunction
     * @return SubcommandData
     */
    fun SubcommandData.setCmdFunction(function: CmdFunction): SubcommandData {
        CmdRouter.FunctionTemp.add(function)
        return this
    }

    /**
     * ### CommandDataに権限を設定します。
     * SubcommandGroup,Subcommandと重複が可能です。
     * その場合、全てのチェックをクリアしないと当該コマンドを使用できません(AND処理)
     * @param permission 設定する権限
     * @return CommandData
     */
    fun CommandData.setPermission(permission: (event: SlashCommandEvent) -> Boolean): CommandData {
        CmdRouter.CmdPermissionTemp = CmdPermission(permission)
        return this
    }

    /**
     * ### SubcommandGroupDataに権限を設定します。
     * Command,Subcommandと重複が可能です(AND処理)
     * @param permission 設定する権限
     * @return SubcommandGroupData
     */
    fun SubcommandGroupData.setPermission(permission: (event: SlashCommandEvent) -> Boolean): SubcommandGroupData {
        CmdRouter.SubcmdGroupPermissionTemp.add(CmdRouter.NameToPermission(this.name, CmdPermission(permission)))
        return this
    }

    /**
     * ### SubcommandDataに権限を設定します。
     * Command,SubcommandGroupと重複が可能です(AND処理)
     * @param permission 設定する権限
     * @return SubcommandData
     */
    fun SubcommandData.setPermission(permission: (event: SlashCommandEvent) -> Boolean): SubcommandData {
        CmdRouter.SubcmdPermissionTemp.add(CmdRouter.NameToPermission(this.name, CmdPermission(permission)))
        return this
    }

    /**
     * ### Command全体をビルドします。
     * 全てのFunction,権限設定を解析し、RouteDataに登録します。
     * @return CommandData
     */
    fun CommandData.build(): CommandData {
        val cmdType: CmdType = when {
            this.subcommands.isEmpty() && this.subcommandGroups.isEmpty() -> Cmd
            this.subcommands.isNotEmpty() && this.subcommandGroups.isEmpty() -> CmdSubcmd
            this.subcommands.isEmpty() && this.subcommandGroups.isNotEmpty() -> CmdSubcmdGroupSubcmd
            else -> throw InvalidPropertiesFormatException("未知のコマンドタイプです！")
        }

        when (cmdType) {
            Cmd -> {
                CmdRouter.RouteData[CmdRouter.CmdIdentifier(this.name, null, null)] =
                    CmdRouter.CmdIndex(CmdRouter.FunctionTemp[0], listOfNotNull(CmdRouter.CmdPermissionTemp))
            }
            CmdSubcmd -> {
                for ((count, subcommand) in this.subcommands.withIndex()) {
                    CmdRouter.RouteData[CmdRouter.CmdIdentifier(this.name, null, subcommand.name)] =
                        CmdRouter.CmdIndex(
                            CmdRouter.FunctionTemp[count],
                            listOfNotNull(
                                CmdRouter.CmdPermissionTemp,
                                CmdRouter.SubcmdPermissionTemp.let { list ->
                                    if (list.firstOrNull()?.name == subcommand.name)
                                        list.removeFirstOrNull()?.permission
                                    else null
                                }
                            )
                        )

                }
            }
            CmdSubcmdGroupSubcmd -> {
                var count = 0
                for (subcommandGroup in this.subcommandGroups)
                    for (subcommand in subcommandGroup.subcommands) {
                        CmdRouter.RouteData[CmdRouter.CmdIdentifier(this.name, subcommandGroup.name, subcommand.name)] =
                            CmdRouter.CmdIndex(
                                CmdRouter.FunctionTemp[count],
                                listOfNotNull(
                                    CmdRouter.CmdPermissionTemp,
                                    CmdRouter.SubcmdGroupPermissionTemp.let { list ->
                                        if (list.firstOrNull()?.name == subcommand.name)
                                            list.removeFirstOrNull()?.permission
                                        else null
                                    },
                                    CmdRouter.SubcmdPermissionTemp.let { list ->
                                        if (list.firstOrNull()?.name == subcommand.name)
                                            list.removeFirstOrNull()?.permission
                                        else null
                                    }
                                )
                            )
                        count++
                    }
            }
        }

        CmdRouter.FunctionTemp.clear()
        CmdRouter.permTempClearAll()
        return this
    }

    /**
     * ### コマンドの構成タイプ
     * - Cmd - /command params:params...
     * - CmdSubcmd - /command subcmd params:params...
     * - CmdSubcmdGroupSubcmd - /command subcmdgroup subcmd params:params...
     */
    enum class CmdType {
        Cmd, CmdSubcmd, CmdSubcmdGroupSubcmd
    }
}

