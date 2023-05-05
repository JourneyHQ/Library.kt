package dev.yuua.journeylib.qnortz

import java.awt.Color

fun code(context: String) = "`$context`"

fun codeBlock(context: String) = "```\n$context\n```"

fun bold(context: String) = "**$context**"

fun italic(context: String) = "*$context*"

fun strike(context: String) = "~~$context~~"

fun spoiler(context: String) = "||$context||"

fun callout(context: String) = "> $context"


enum class QnortzColor(val color: Color) {
    Pink(Color(245, 189, 230)),
    Red(Color(237, 135, 150)),
    Green(Color(166, 218, 149)),
    Yellow(Color(238, 212, 159));

    fun int() = color.rgb
}