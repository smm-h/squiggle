package ir.smmh.lingu

import java.io.File
import java.net.URI

class Code private constructor(
    val string: String,
    val language: Language?,
    val file: File?,
) {

    constructor(string: String, language: Language?) : this(string, language, null)

    constructor(file: File, language: Language? = Language.of(file.extension)) : this(
        if (file.exists()) file.readText()
        else throw Language.Exception("file not found: '$file'"), language, file
    )

    // constructor(string: String, langExt: String) : this(string, Language.of(langExt))
    // constructor(file: File) : this(file.readText(), Language.of(file.extension), file)
    // constructor(filename: String) : this(File(filename))

    private val aspects: MutableMap<Aspect<*>, Any> = HashMap()

    fun interface Process : (Code) -> Unit {

        interface HasRequirements : Process {
            fun checkRequirements(code: Code): Boolean
        }

        operator fun plus(that: Process): Process {
            if (this == reset || that == reset) throw Language.Exception("ought not add manual reset")
            val merged = mutableListOf<Process>()
            if (this is List) this.processes.forEach { merged.add(it) } else merged.add(this)
            if (that is List) that.processes.forEach { merged.add(it) } else merged.add(that)
            return List(merged)
        }

        companion object {
            val empty: Code.Process = List(emptyList())
        }

        /**
         * Immutable list of processes
         */
        private class List(val processes: kotlin.collections.List<Process>) : Process.HasRequirements,
            Iterable<Process> {
            override fun iterator(): Iterator<Process> =
                processes.iterator()

            override fun checkRequirements(code: Code): Boolean {
                for (process in processes)
                    if (process is HasRequirements)
                        if (!process.checkRequirements(code))
                            return false
                return true
            }

            override fun invoke(code: Code) {
                reset(code)
                for (process in processes) {
                    try {
                        process(code)
                    } catch (e: Language.Exception) {
                        code.issue(e.toMishap())
                    }
                    if (code.processFailed) break
                }
                code.getNullable(Mishaps)?.joinToString("\n")?.let { println(it) }
            }
        }
    }

    class Aspect<T : Any>(val name: String) {
        override fun toString() = name
    }

    abstract class Mishap {
        abstract val token: Token?
        abstract val message: String
        abstract val level: Level
        abstract val fatal: Boolean

        class Impl(
            override val token: Token?,
            override val message: String,
            override val level: Level,
            override val fatal: Boolean
        ) : Mishap()

        enum class Level {
            EXCEPTION, ERROR, WARNING, WEAK_WARNING, TYPO, INFO
        }

        override fun toString() = message
    }

    var processFailed = false
        private set

    fun issue(
        token: Token,
        message: String,
        level: Mishap.Level = Mishap.Level.ERROR,
        fatal: Boolean = true
    ) = issue(Mishap.Impl(token, message, level, fatal))

    fun issue(mishap: Mishap) {
        if (Mishaps !in aspects) aspects[Mishaps] = ArrayList<Mishap>()
        this[Mishaps].add(mishap)
        if (mishap.fatal) processFailed = true
    }

    /**
     * Something named, that is defined in code, and can be referenced elsewhere
     *
     * Has its down tooltip/docs that can be shown when hovered over
     *
     * Depends on the language
     */
    interface Defined {
        val name: String
        val code: Code
        val position: Int
    }

    companion object {

        val Mishaps = Aspect<MutableList<Mishap>>("mishaps")
        val Links = Aspect<Map<IntRange, URI>>("links")
        val References = Aspect<Map<IntRange, Defined>>("references")

        val reset = object : Code.Process {
            override fun invoke(code: Code) {
                code.processFailed = false
                code.aspects.clear()
            }
        }
    }

    operator fun contains(aspect: Aspect<*>): Boolean = aspect in aspects

    operator fun <T : Any> get(aspect: Aspect<T>) = getNullable(aspect)!!

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getNullable(aspect: Aspect<T>) = aspects[aspect] as T?

    operator fun <T : Any> set(aspect: Aspect<T>, value: T?) {
        if (value == null)
            aspects.remove(aspect)
        else
            aspects[aspect] = value
    }

    override fun toString(): String {
        val f = file
        return if (f == null) {
            val l = language
            if (l is Language.HasFileExt) "*.${l.fileExt}" else "*.*"
        } else f.name
    }

    fun getInsights() = aspects.entries.joinToString("\n") {
        it.key.name + "\t" + it.value
    }
}