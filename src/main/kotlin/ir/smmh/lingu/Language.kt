package ir.smmh.lingu

import ir.smmh.helium.Helium
import ir.smmh.lingu.Language.*
import ir.smmh.markup.Html
import ir.smmh.markup.Markdown
import ir.smmh.serialization.json.Json
import java.io.File

/**
 * A [Language] is a formal way to employ your computer to accomplish a certain
 * goal. All languages operate on [Code] objects which are abstractions over
 * files or plain strings. Categories of languages include:
 *
 * - [Construction]: tries to construct something from `Code` (e.g. [Helium])
 * - [Serialization]: serializes any object into `Code` and back (e.g. [Json])
 * - [Markup]: compiles a [Document] into `Code` (e.g. [Markdown] and [Html])
 *
 * This interface is not suitable for natural languages.
 */
interface Language {
    interface HasFileExt : Language {
        val fileExt: String

        abstract class Impl(override val fileExt: String) : HasFileExt {
            init {
                languages[fileExt] = this
            }
        }

        fun bindFileExt(url: String) =
            bindFileExt(url, fileExt)

        companion object {
            fun bindFileExt(url: String, fileExt: String) = url
                .replace(lateFileExt, fileExt.lowercase())
                .replace(lateFileExtUpper, fileExt.uppercase())
        }
    }

    class Exception(message: String) : kotlin.Exception(message) {
        fun toMishap() = object : Code.Mishap() {
            override val token = null
            override val fatal = true
            override val level = Level.EXCEPTION
            override val message = this@Exception.message!!
        }
    }

    companion object {
        const val lateFileExt = "%ext%"
        private const val lateFileExtUpper = "%EXT%"
        private val languages: MutableMap<String, Language> = HashMap()
        fun of(langExt: String): Language =
            languages[langExt] ?: throw Language.Exception("no language for '$langExt'")
    }

    fun code(string: String) =
        Code(string, this)

    fun code(file: File) =
        Code(file, this)

    interface Processable : Language {
        val process: Code.Process
    }

    interface Construction<T : Any> : Processable {
        val construction: Code.Aspect<T>
        operator fun get(code: Code): T? {
            process.invoke(code)
            return code.getNullable(construction)
        }
    }

    interface Serialization : Language {
        fun serialize(it: Any?): String
        fun deserialize(string: String): Any?

        fun serializeToCode(it: Any?): Code = Code(serialize(it), this)
        fun deserialize(code: Code) {
            code[Deserialization] = deserialize(code.string)
        }

        companion object {
            val Deserialization = Code.Aspect<Any>("deserialization")
        }
        // TODO intermediate representation?
    }

    interface Markup : Language {
        abstract fun compile(document: ir.smmh.markup.Markup.Document, metadata: String?): String
        abstract infix fun compile(it: ir.smmh.markup.Markup.Text): String
        fun code(document: ir.smmh.markup.Markup.Document): Code = Code(compile(document), this)
    }
}