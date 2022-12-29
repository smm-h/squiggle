package ir.smmh.util

import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.util.*


object FileUtil {
    /**
     * Creates all the missing directories in a filename.
     *
     * @param filename the name of a file whose parent directories are to be created
     * @return the same as the entered filename
     * @throws IOException          if the directories cannot be created
     * @throws InvalidPathException if the filename provided is not a valid path
     */
    fun touch(filename: String) = filename.also { Path.of(it).parent?.also { Files.createDirectories(it) } }

    fun getExt(filename: String) = filename.substring(filename.lastIndexOf('.') + 1).lowercase(Locale.getDefault())

    infix fun File.open(bookmark: String) {
        Desktop.getDesktop().open(File(canonicalPath + if (bookmark.isEmpty()) "" else "#$bookmark"))
    }

    infix fun String.writeTo(filename: String): File {
        return this writeTo File(filename)
    }

    infix fun String.writeTo(file: File): File {
        file.writeText(this)
        return file
    }
}