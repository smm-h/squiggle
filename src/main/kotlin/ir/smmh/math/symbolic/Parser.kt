package ir.smmh.math.symbolic

import ir.smmh.lingu.Token
import ir.smmh.nilex.NiLexLanguage
import ir.smmh.nilex.Sexp

object Parser {
    fun parse(string: String): Token.Structure = sexp.parse(string)
    private val sexp = Sexp(
        "(", ")",
        """
        streak '\t\n\r ' as whitespace
        streak '[0-9]' as digits
        streak '[0-9].' as digitsWithDot
        streak '[A-Z][a-z][0-9]_' as id
        keep '"' ... '"' as string
        keep '//' ... '\n' as comment
        keep '/*' ... '*/' as multiLineComment
        """,
        NiLexLanguage.createFilter("opener", "closer", "whitespace", "comment", "multiLineComment")
    )
}