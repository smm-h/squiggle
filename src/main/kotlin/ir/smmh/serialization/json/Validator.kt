package ir.smmh.serialization.json

import java.io.File
import kotlin.system.exitProcess


/**
 * Meant to be used with [the Json test suite repo](https://github.com/nst/JSONTestSuite)
 */
object Validator {
    fun main(args: Array<String>) {
        exitProcess(
            try {
                Json.parse(File(args[0]).readText())
                0 // accepted
            } catch (e: Json.Exception) {
                1 // rejected
            } catch (e: Throwable) {
                2 // crashed
            }
        )
    }
}