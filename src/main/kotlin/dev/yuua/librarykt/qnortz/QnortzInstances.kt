package dev.yuua.librarykt.qnortz

object QnortzInstances {
    private val instances = hashMapOf<String, Qnortz>()

    operator fun get(key: String): Qnortz {
        return instances[key] ?: throw NoSuchElementException("No instance with such a name was found!")
    }

    operator fun set(key: String, value: Qnortz) {
        instances[key] = value
    }

    fun remove(key: String) {
        instances.remove(key)
    }

    fun exists(key: String): Boolean {
        return instances.contains(key)
    }
}
