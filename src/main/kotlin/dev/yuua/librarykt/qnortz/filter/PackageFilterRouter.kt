package dev.yuua.librarykt.qnortz.filter

class PackageFilterRouter<T>(private val rootPackage: String, packageFilter: PackageFilter<T>) {
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
        println(filterPaths.map { it.key })
    }

    /**
     * Collect all [Filter]s matching the specified [path].
     * @param path The **FULL** path to the package.
     * @return [Filter]s
     */
    fun findAll(path: String?): List<Filter<T>> {
        if (path == null) return emptyList()

        val rootPackageSkip = rootPackage.split(".").size
        val splitFullPath = path.split(".")
        val splitPath = splitFullPath.subList(rootPackageSkip, splitFullPath.size)
        val filters = mutableListOf<Filter<T>>()

        repeat(splitPath.size) {
            val filter = filterPaths[splitPath.subList(0, it + 1).joinToString(".")]
            if (filter != null) filters += filter
        }

        return filters.toList()
    }
}
