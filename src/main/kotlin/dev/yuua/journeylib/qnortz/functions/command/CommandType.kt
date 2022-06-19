package dev.yuua.journeylib.qnortz.functions.command

/**
 * Types of command structure.
 */
enum class CommandStructureType {
    CommandType, SubcommandType, SubcommandGroupType
}

/**
 * Types of command.
 */
enum class CommandFromType {
    TextCommand, SlashCommand
}
