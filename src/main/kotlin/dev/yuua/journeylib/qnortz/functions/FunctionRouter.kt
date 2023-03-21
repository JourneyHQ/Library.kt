package dev.yuua.journeylib.qnortz.functions

interface FunctionRouter<T, F, E> {
    val routes: HashMap<T, F>

    fun inferType(context: String): E

    operator fun set(route: T, function: F) {
        routes[route] = function
    }

    operator fun get(route: T): F
}
