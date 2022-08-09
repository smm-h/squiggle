package ir.smmh.lingu

import java.io.File
import java.util.*

abstract class BaseApp {

    abstract val language: Language

    fun mainDelegate(args: Array<String>) {
        if (args.isEmpty()) {
            beforeAllInteractions()
            Scanner(System.`in`).use {
                while (true) {
                    print(">>> ")
                    val input = it.nextLine()
                    val code = Code(input, language)
                    try {
                        beforeEachInteraction()
                        processInteraction(code)
                        afterEachInteraction()
                    } catch (signal: StopIterationSignal) {
                        break
                    }
                }
            }
            afterAllInteractions()
        } else {
            val filename = args[0]
            val options: List<String> = args.drop(1)
            val code = Code(File(filename), language)
            process(code, filename, options)
        }
    }

    abstract fun beforeAllInteractions()

    abstract fun afterAllInteractions()

    abstract fun beforeEachInteraction()

    abstract fun afterEachInteraction()

    abstract fun processInteraction(code: Code)

    abstract fun process(code: Code, filename: String, options: List<String> = emptyList())

    abstract class Adapter : BaseApp() {
        override fun beforeAllInteractions() {
            // do nothing
        }

        override fun afterAllInteractions() {
            // do nothing
        }

        override fun beforeEachInteraction() {
            // do nothing
        }

        override fun afterEachInteraction() {
            // do nothing
        }

        override fun processInteraction(code: Code) {
            println(code.getInsights())
        }

        override fun process(code: Code, filename: String, options: List<String>) {
            processInteraction(code)
        }
    }

    class StopIterationSignal(message: String) : Exception(message)
}