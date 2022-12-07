package ir.smmh.helium

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Token
import ir.smmh.lingu.Tokenizer
import ir.smmh.nilex.NiLexLanguage.FilteredTokens
import ir.smmh.nilex.NiLexLanguage.filterOut
import ir.smmh.nilex.NiLexTokenizerFactory
import java.io.File

// TODO testing and debugging
/**
 * Homoiconic I/O Language
 */
object Helium : Language.Processable {
    private val tokenize: Tokenizer =
        NiLexTokenizerFactory.create(File("res/helium/helium.nlx"))

    override val process: Code.Process = tokenize +
            filterOut("opener", "closer", "whitespace", "comment", "multiLineComment") + { code ->
        val statements = ArrayList<Token.Structure>()
        val tokens = code[FilteredTokens]
    }
}

fun main() {
    val code = Helium.code(File("res/helium/hello-world"))
    Helium.process(code)
}