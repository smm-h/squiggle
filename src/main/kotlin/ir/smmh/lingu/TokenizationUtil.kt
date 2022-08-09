package ir.smmh.lingu

import ir.smmh.util.FunctionalUtil.toSet


object TokenizationUtil {
    class Exception(message: String, val token: Token? = null) : kotlin.Exception(message)

    val DIGITS = "0123456789".toCharSet()
    val LETTERS_LOWER = "abcdefghijklmnopqrstuvwxyz".toCharSet()
    val LETTERS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharSet()
    val LETTERS = LETTERS_LOWER + LETTERS_UPPER

    /** https://github.com/JetBrains/JetBrainsMono/wiki/List-of-supported-symbols#monospace-ligatures */
    val LIGATURES = setOf(
        "--",
        "---",
        "==",
        "===",
        "!=",
        "!==",
        "=!=",
        "=:=",
        "=/=",
        "<=",
        ">=",
        "&&",
        "&&&",
        "&=",
        "++",
        "+++",
        "***",
        ";;",
        "!!",
        "??",
        "???",
        "?:",
        "?.",
        "?=",
        "<:",
        ":<",
        ":>",
        ">:",
        "<:<",
        "<>",
        "<<<",
        ">>>",
        "<<",
        ">>",
        "||",
        "-|",
        "_|_",
        "|-",
        "||-",
        "|=",
        "||=",
        "##",
        "###",
        "####",
        "#{",
        "#[",
        "]#",
        "#(",
        "#?",
        "#_",
        "#_(",
        "#:",
        "#!",
        "#=",
        "^=",
        "<$>",
        "<$",
        "$>",
        "<+>",
        "<+",
        "+>",
        "<*>",
        "<*",
        "*>",
        "</",
        "</>",
        "/>",
        "<!--",
        "<#--",
        "-->",
        "->",
        "->>",
        "<<-",
        "<-",
        "<=<",
        "=<<",
        "<<=",
        "<==",
        "<=>",
        "<==>",
        "==>",
        "=>",
        "=>>",
        ">=>",
        ">>=",
        ">>-",
        ">-",
        "-<",
        "-<<",
        ">->",
        "<-<",
        "<-|",
        "<=|",
        "|=>",
        "|->",
        "<->",
        "<~~",
        "<~",
        "<~>",
        "~~",
        "~~>",
        "~>",
        "~-",
        "-~",
        "~@",
        "[||]",
        "|]",
        "[|",
        "|}",
        "{|",
        "[<",
        ">]",
        "|>",
        "<|",
        "||>",
        "<||",
        "|||>",
        "<|||",
        "<|>",
        "...",
        "..",
        ".=",
        "..<",
        ".?",
        "::",
        ":::",
        ":=",
        "::=",
        ":?",
        ":?>",
        "//",
        "///",
        "/*",
        "*/",
        "/=",
        "//=",
        "/==",
        "@_",
        "__"
    )


    fun String.toCharSet() = asIterable().toSet { it }

    fun String.toStringSet() = asIterable().toSet { it.toString() }

    fun String.isComprisedOnlyOf(charSet: Set<Char>): Boolean = toCharSet().union(charSet) == charSet

    fun visualizeWhitespace(whitespace: String) = whitespace.asIterable().joinToString("", "(", ")") {
        when (it) {
            '\n' -> "\\n"
            '\t' -> "\\t"
            '\r' -> "\\r"
            ' ' -> "-"
            else -> "?"
        }
    }
}