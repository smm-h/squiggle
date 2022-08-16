package ir.smmh.helium

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Tokenizer
import ir.smmh.lingu.Tokenizer.Companion.tokens
import ir.smmh.nilex.NiLexLanguage
import ir.smmh.nilex.NiLexTokenizerFactory
import java.io.File

// TODO testing and debugging
/**
 * Homoiconic I/O Language
 */
object Helium : Language.Processable {
    private val tokenizer: Tokenizer =
        NiLexTokenizerFactory().apply { load(NiLexLanguage.code(File("res/helium/helium.nlx"))) }()


    override val process: Code.Process = tokenizer + { code ->
        println(tokens of code)
    }
}

fun main() {
    val code = Helium.code(File("res/helium/hello-world"))
    Helium.process(code)
}