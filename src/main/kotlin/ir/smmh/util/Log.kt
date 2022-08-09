package ir.smmh.util

import ir.smmh.util.FileUtil.touch
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintStream
import java.nio.file.InvalidPathException
import java.time.Instant

fun interface Log {
    fun getPrintStream(): PrintStream
    fun log() {
        getPrintStream().println()
    }

    fun log(text: String) {
        val log = getPrintStream()
        log.print(Instant.now())
        log.print(" \t ")
        log.println(text)
    }

    fun log(error: Throwable) {
        val message = error.message
        message?.let { log(it) }
    }

    private class Impl(private val printStream: PrintStream) : Log {
        override fun getPrintStream(): PrintStream {
            return printStream
        }
    }

    companion object {
        fun fromFile(filename: String, defaultStream: PrintStream): Log {
            try {
                return Impl(PrintStream(FileOutputStream(touch(filename), true)))
            } catch (e: FileNotFoundException) {
                System.err.println("File not found: $filename")
            } catch (e: InvalidPathException) {
                System.err.println("Invalid filename: $filename")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return fromStream(defaultStream)
        }

        // TODO replace all System.out.print with out.log
        // (and all System.err.print with err.log)
        fun fromStream(stream: PrintStream): Log {
            return Impl(stream)
        }
    }
}