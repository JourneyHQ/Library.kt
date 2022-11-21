package dev.yuua.journeylib.qnortz.filter

class PackageFilter<T>(
    val path: String? = null,
    val filter: Filter<T> = Filter(),
    builder: PackageFilter<T>.() -> Unit
) {
    val paths = mutableListOf<PackageFilter<T>>()

    fun path(path: String? = null, filter: Filter<T> = Filter(), builder: PackageFilter<T>.() -> Unit) {
        paths.add(PackageFilter(path, filter, builder))
    }

    init {
        builder()
    }
}


