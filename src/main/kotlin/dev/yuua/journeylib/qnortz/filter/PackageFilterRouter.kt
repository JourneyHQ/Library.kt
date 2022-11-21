package dev.yuua.journeylib.qnortz.filter

class PackageFilterRouter<T>(packageFilter: PackageFilter<T>) {
    private val filterPaths = hashMapOf<String?, Filter<T>>()

    private fun expandPackageFilter(prvPath: String? = null, packageFilter: PackageFilter<T>) {
        for (path in packageFilter.paths) {
            val prvPathPrefix = if (prvPath != null) "$prvPath." else ""
            val currentPath = prvPathPrefix + path.path
            filterPaths[currentPath] = path.filter
            expandPackageFilter(currentPath, path)
        }
    }

    init {
        expandPackageFilter(packageFilter = packageFilter)
    }

    /**
     * Collect all [Filter]s matching the specified [path].
     * @param path The path to the package.
     * @return [Filter]s
     */
    fun findAll(path: String): List<Filter<T>> {
        val splitPath = path.split(".")
        val filters = mutableListOf<Filter<T>>()

        repeat(splitPath.size) {
            val filter = filterPaths[splitPath.subList(0, it).joinToString(".")]
            if (filter != null) filters += filter
        }

        return filters.toList()
    }
}
