package ir.smmh.lingu

import ir.smmh.nile.Mut
import ir.smmh.nile.Named
import java.io.File
import java.net.URI

class Code private constructor(
    string: String,
    language: Language?,
    file: File?,
    override val mut: Mut = Mut(),
) : Mut.Able {

    constructor(string: String, language: Language?) : this(string, language, null)

    constructor(file: File, language: Language? = Language.of(file.extension)) : this(
        if (file.exists()) file.readText()
        else throw Language.Exception("file not found: '$file'"), language, file
    )

    // constructor(string: String, langExt: String) : this(string, Language.of(langExt))
    // constructor(file: File) : this(file.readText(), Language.of(file.extension), file)
    // constructor(filename: String) : this(File(filename))

    var string: String = string
        private set(value) {
            if (field != value) {
                mut.preMutate()
                field = value
                mut.mutate()
            }
        }
    var language: Language? = language
        set(value) {
            if (field != value) {
                mut.preMutate()
                field = value
                mut.mutate()
            }
        }
    var file: File? = file
        set(value) {
            if (field != value) {
                mut.preMutate()
                field = value
                mut.mutate()
            }
        }

    val canBeProcessed: Boolean get() = language is Language.Processable
    fun beProcessed() {
        val it = language
        if (it is Language.Processable) it.process(this)
    }

    val canBeConstructed: Boolean get() = language is Language.Construction<*>
    fun <T> beConstructed(): T? {
        val it = language
        try {
            @Suppress("UNCHECKED_CAST")
            if (it is Language.Construction<*>) return it[this] as T
        } catch (_: ClassCastException) {
        }
        return null
    }

    val canBeDeserialized: Boolean get() = language is Language.Serialization
    fun beDeserialized(): Any? {
        val it = language
        if (it is Language.Serialization) it.deserialize(this)
        return this[Language.Serialization.deserialization]
    }

    private val aspects: MutableMap<Aspect<*>, Any> = HashMap()

    fun interface Process : (Code) -> Unit {
        abstract class Named(override val name: String) : ir.smmh.nile.Named, Process {
            override fun toString() = name
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
        private class List(val processes: kotlin.collections.List<Process>) : Process, Iterable<Process> {
            override fun iterator(): Iterator<Process> =
                processes.iterator()

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
                (mishaps of code)?.joinToString("\n")?.let { println(it) }
            }
        }
    }

    class Aspect<T>(override val name: String) : Named {
        infix fun of(code: Code): T? = code[this]
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
        if (mishaps !in aspects) aspects[mishaps] = ArrayList<Mishap>()
        (mishaps of this)!!.add(mishap)
        if (mishap.fatal) processFailed = true
    }

    /**
     * Something named, that is defined in code, and can be referenced elsewhere
     *
     * Has its down tooltip/docs that can be shown when hovered over
     *
     * Depends on the language
     */
    interface Defined : Named {
        val code: Code
        val position: Int
    }

    companion object {

        val mishaps = Aspect<MutableList<Mishap>>("mishaps")
        val links = Aspect<Map<IntRange, URI>>("links")
        val references = Aspect<Map<IntRange, Defined>>("references")

        val reset = object : Code.Process.Named("CODE-RESET") {
            override fun invoke(code: Code) {
                code.processFailed = false
                code.aspects.clear()
            }
        }
    }

    operator fun contains(aspect: Aspect<*>): Boolean = aspect in aspects

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(aspect: Aspect<T>): T? {
        return aspects[aspect] as T
    }

    operator fun <T> set(aspect: Aspect<T>, value: T?) {
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