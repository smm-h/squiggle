package ir.smmh.helium

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Token
import ir.smmh.lingu.Tokenizer
import ir.smmh.lingu.Tokenizer.Companion.Tokens
import ir.smmh.nilex.NiLexLanguage
import ir.smmh.nilex.NiLexTokenizerFactory
import ir.smmh.tree.impl.NodedSpecificTreeImpl
import java.io.File

// TODO testing and debugging
/**
 * Homoiconic I/O Language
 */
object Helium : Language.Processable {
    private val tokenizer: Tokenizer =
        NiLexTokenizerFactory().apply { load(NiLexLanguage.code(File("res/helium/helium.nlx"))) }()

    private val ignore = setOf("whitespace", "opener", "closer")

    private fun filter(token: Token): Boolean {
        for (tag in ignore)
            if (tag in token.type)
                return false
        return true
    }

    override val process: Code.Process = tokenizer + { code ->
        val tree = NodedSpecificTreeImpl<Token>()
        tree.rootData = Token.ROOT
        val topLevelTokens = tree.rootNode!!.children
        val tokens = (Tokens of code)!!.filter(::filter)

    }
}

fun main() {
    val code = Helium.code(File("res/helium/hello-world"))
    Helium.process(code)
}