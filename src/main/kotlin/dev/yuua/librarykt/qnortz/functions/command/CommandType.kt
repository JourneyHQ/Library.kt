package dev.yuua.librarykt.qnortz.functions.command

/**
 * Types of command structure.
 */
enum class CommandStructureType {
    CommandType, SubcommandType, SubcommandGroupType
}

/**
 * Types of command.
 */
enum class CommandMethodType {
    TextCommand, SlashCommand
}
