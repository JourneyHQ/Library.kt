package dev.yuua.journeylib.discord.framework.function

import dev.yuua.journeylib.universal.LibFlow
import org.reflections.Reflections

interface FrFunctionManagerStruct<T : FrFunctionStruct> {
    val instances get() = mutableListOf<T>()

    fun record(script: FrFunctionManagerStruct<T>.() -> Unit) {
        script(this)
    }
}

inline fun <reified T : FrFunctionStruct> FrFunctionManagerStruct<T>.init(targetPackage: String) {
    val libFlow = LibFlow()
    val classes = Reflections(targetPackage).getSubTypesOf(T::class.java)

    if (classes.isEmpty())
        throw UnsupportedOperationException("Package:$targetPackage was empty or not found!")

    val functionClasses = classes.filter {
        it.enclosingClass == null && !it.name.contains("$")
    }.onEach {
        libFlow.success("${T::class.simpleName}:${it.simpleName} queued!")
    }

    functionClasses.forEach { instances.add(it.getConstructor().newInstance() as T) }
}
