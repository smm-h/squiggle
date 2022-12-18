package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Token
import ir.smmh.nilex.NiLexLanguage.FilteredTokens
import ir.smmh.nilex.NiLexLanguage.assertBalance
import ir.smmh.nilex.NiLexLanguage.filterOut
import ir.smmh.nilex.NiLexTokenizer.Companion.v

class Sexp(val opener: String, val closer: String, tokenizer: String, filter: (Token) -> Boolean) :
    Language.Representation<Token.Structure>, Language.Processable {

    override val parsed = Code.Aspect<Token.Structure>("root")
    override fun parse(string: String): Token.Structure =
        code(string).run { process(this); this[parsed] }

    override fun represent(it: Token.Structure): String = when (it) {
        is Token.Structure.Leaf -> it.token.data
        is Token.Structure.Node -> it.list.joinToString(" ", opener, closer, transform = ::represent)
    }

    private val pOpen = v(opener)
    private val pClose = v(closer)
    private val tokenize = NiLexTokenizerFactory.create(tokenizer + "verbatim '$opener' verbatim '$closer'")

    private class Frame(val parent: Frame?) {
        val list: MutableList<Token.Structure> = ArrayList()
    }

    override val process: Code.Process = tokenize + assertBalance(opener, closer) + filterOut(filter) + { code ->
        var curr: Frame = Frame(null)
        for (token in code[FilteredTokens]) {
            when (token.type.name) {
                pOpen -> curr = Frame(curr)
                pClose -> {
                    val prev = curr
                    curr = prev.parent!!
                    curr.list.add(Token.Structure.Node(prev.list))
                }
                else -> curr.list.add(Token.Structure.Leaf(token))
            }
        }
        code[parsed] = Token.Structure.Node(curr.list)
    }
}