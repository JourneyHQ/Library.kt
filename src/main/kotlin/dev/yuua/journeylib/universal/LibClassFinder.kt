package dev.yuua.journeylib.universal

import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URL
import java.util.*

class LibClassFinder {
    private val classLoader: ClassLoader = Thread.currentThread().contextClassLoader

    private fun isClassFile(fileName: String): Boolean {
        return fileName.endsWith(".class")
    }

    private fun fileToClass(name: String): String {
        return name.substring(0, name.length - ".class".length)
    }

    private fun packageToClass(resourceName: String): String {
        return fileToClass(resourceName).replace('/', '.')
    }

    private fun packageToResource(packageName: String): String {
        return packageName.replace('.', '/')
    }

    fun packageExists(packageName: String): Boolean {
        return try {
            find(packageName).isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    fun find(rootPackageName: String): List<Class<*>> {
        val url = classLoader
            .getResource(packageToResource(rootPackageName)) ?: return ArrayList()

        return when (val protocol = url.protocol) {
            "file" -> findFromFile(rootPackageName, File(url.file))
            "jar" -> findFromJar(rootPackageName, url)
            else -> throw IllegalArgumentException("Unsupported Class Load Protocol[$protocol]")
        }
    }

    @Throws(ClassNotFoundException::class)
    private fun findFromFile(packageName: String, dir: File): List<Class<*>> {
        val classes = emptyArray<Class<*>>().toMutableList()
        for (path in Objects.requireNonNull(dir.list())) {
            val entry = File(dir, path)
            if (entry.isFile && isClassFile(entry.name)) {
                classes.add(classLoader.loadClass(packageName + "." + fileToClass(entry.name)))
            } else if (entry.isDirectory) {
                classes.addAll(findFromFile(packageName + "." + entry.name, entry))
            }
        }
        return classes
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun findFromJar(rootPackageName: String, jarFileUrl: URL): List<Class<*>> {
        val classes = emptyArray<Class<*>>().toMutableList()
        val jarUrlConnection = jarFileUrl.openConnection() as JarURLConnection
        jarUrlConnection.jarFile.use { jarFile ->
            val jarEnum = jarFile.entries()
            while (jarEnum.hasMoreElements()) {
                val jarEntry = jarEnum.nextElement()
                if (jarEntry.name.startsWith(packageToResource(rootPackageName))
                    && isClassFile(jarEntry.name))
                    classes.add(classLoader.loadClass(packageToClass(jarEntry.name)))

            }
        }
        return classes
    }
}