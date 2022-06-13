package dev.yuua.journeylib.qnortz.limit

data class LimitRouter<T>(val basePackage: String, val limits: List<Pair<String, Limit<T>>>) {

    /**
     * Returns [Limit] for the specified package
     * If [Limit] for the package is not exists, returns the default value of [Limit]
     * @param packageName name of package
     */
    operator fun get(packageName: String): Limit<T> {
        return limits.firstOrNull {
            "$basePackage.${it.first}" == packageName
        }?.second ?: Limit()
    }
}
