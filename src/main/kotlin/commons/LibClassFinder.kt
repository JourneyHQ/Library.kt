package dev.yuua.journeylib.commons

import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URL
import java.util.*

class LibClassFinder {
    private val classLoader: ClassLoader = Thread.currentThread().contextClassLoader

    private fun fileNameToClassName(name: String): String {
        return name.substring(0, name.length - ".class".length)
    }

    private fun resourceNameToClassName(resourceName: String): String {
        return fileNameToClassName(resourceName).replace('/', '.')
    }

    private fun isClassFile(fileName: String): Boolean {
        return fileName.endsWith(".class")
    }

    private fun packageNameToResourceName(packageName: String): String {
        return packageName.replace('.', '/')
    }

    @Throws(ClassNotFoundException::class, IOException::class)
    fun findClasses(rootPackageName: String): List<Class<*>> {
        val resourceName = packageNameToResourceName(rootPackageName)
        val url = classLoader.getResource(resourceName) ?: return ArrayList()
        val protocol = url.protocol
        if ("file" == protocol) {
            return findClassesWithFile(rootPackageName, File(url.file))
        } else if ("jar" == protocol) {
            return findClassesWithJarFile(rootPackageName, url)
        }
        throw IllegalArgumentException("Unsupported Class Load Protocol[$protocol]")
    }

    @Throws(ClassNotFoundException::class)
    private fun findClassesWithFile(packageName: String, dir: File): List<Class<*>> {
        val classes: MutableList<Class<*>> = ArrayList()
        for (path in Objects.requireNonNull(dir.list())) {
            val entry = File(dir, path)
            if (entry.isFile && isClassFile(entry.name)) {
                classes.add(classLoader.loadClass(packageName + "." + fileNameToClassName(entry.name)))
            } else if (entry.isDirectory) {
                classes.addAll(findClassesWithFile(packageName + "." + entry.name, entry))
            }
        }
        return classes
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun findClassesWithJarFile(rootPackageName: String, jarFileUrl: URL): List<Class<*>> {
        val classes: MutableList<Class<*>> = ArrayList()
        val jarUrlConnection = jarFileUrl.openConnection() as JarURLConnection
        jarUrlConnection.jarFile.use { jarFile ->
            val jarEnum = jarFile.entries()
            val packageNameAsResourceName = packageNameToResourceName(rootPackageName)
            while (jarEnum.hasMoreElements()) {
                val jarEntry = jarEnum.nextElement()
                if (jarEntry.name.startsWith(packageNameAsResourceName) && isClassFile(jarEntry.name)) {
                    classes.add(classLoader.loadClass(resourceNameToClassName(jarEntry.name)))
                }
            }
        }
        return classes
    }
}