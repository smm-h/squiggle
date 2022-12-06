package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Token
import ir.smmh.nilex.NiLexLanguage.FilteredTokens
import ir.smmh.nilex.NiLexLanguage.assertBalance
import ir.smmh.nilex.NiLexLanguage.filterOut
import ir.smmh.nilex.NiLexTokenizer.Companion.v

class Sexp(opener: String, closer: String, tokenizer: String, filter: (Token) -> Boolean) :
    Language.Construction<Token.Structure> {

    override val construction = Code.Aspect<Token.Structure>("root")

    private val pOpen = v(opener)
    private val pClose = v(closer)
    private val tokenize = NiLexTokenizerFactory.create(tokenizer + "verbatim '$opener' verbatim '$closer'")

    private class Frame(val parent: Frame?) {
        val list: MutableList<Token.Structure> = ArrayList()
    }

    override val process: Code.Process = tokenize + assertBalance(opener, closer) + filterOut(filter) + { code ->
        var curr: Frame = Frame(null)
        for (token in FilteredTokens of code) {
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
        code[construction] = Token.Structure.Node(curr.list)
    }
}