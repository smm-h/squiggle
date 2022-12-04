package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Token
import ir.smmh.lingu.Tokenizer

object Sexp : Language.Construction<Token.Structure> {

    override val construction = Code.Aspect<Token.Structure>("root")

    private val tokenize: Tokenizer = NiLexTokenizerFactory.create(
        """
        streak '\t\n\r ' as whitespace
        streak '[0-9]' as digits
        streak '[A-Z][a-z][0-9]_' as id
        keep '"' ... '"' as string
        keep '//' ... '\n' as comment
        keep '/*' ... '*/' as multiLineComment
        verbatim '('
        verbatim ')'
        """
    )

    private class Frame(val parent: Frame?) {
        val list: MutableList<Token.Structure> = ArrayList()
    }

    private val assertBalance = NiLexLanguage.assertBalance("(", ")", NiLexLanguage.FilteredTokens)
    private val pOpen = NiLexTokenizer.v("(")
    private val pClose = NiLexTokenizer.v(")")

    override val process: Code.Process = tokenize +
            NiLexLanguage.filterOut("opener", "closer", "whitespace", "comment", "multiLineComment") +
            assertBalance + { code ->
        val queue = ArrayDeque((NiLexLanguage.FilteredTokens of code)!!)
        var currentFrame: Frame = Frame(null)
        while (queue.isNotEmpty()) {
            val token = queue.removeFirst()
            when (token.type.name) {
                pOpen -> currentFrame = Frame(currentFrame)
                pClose -> {
                    val previousFrame = currentFrame
                    currentFrame = previousFrame.parent!!
                    currentFrame.list.add(Token.Structure.Node(previousFrame.list))
                }
                else -> currentFrame.list.add(Token.Structure.Leaf(token))
            }
        }
        code[construction] = Token.Structure.Node(currentFrame.list)
    }
}