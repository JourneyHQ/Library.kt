package dev.yuua.journeylib.discord.framework

import dev.yuua.journeylib.universal.LibClassFinder

object FrameworkManager {
    lateinit var commandPackage: String
    lateinit var eventPackage: String

    fun setCommandPackage(value: String): FrameworkManager {
        if (LibClassFinder().findClasses(value).isEmpty())
            throw UnsupportedOperationException("Package:$value が空か、見つかりませんでした。")
        commandPackage = value
        return this
    }

    fun setEventPackage(value: String): FrameworkManager {
        if (LibClassFinder().findClasses(value).isEmpty())
            throw UnsupportedOperationException("Package:$value が空か、見つかりませんでした。")
        eventPackage = value
        return this
    }
}