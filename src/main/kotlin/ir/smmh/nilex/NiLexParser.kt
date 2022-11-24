package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Parser
import ir.smmh.lingu.Token
import ir.smmh.lingu.Tokenizer.Companion.Tokens
import ir.smmh.tree.Tree
import ir.smmh.tree.impl.NodedSpecificTreeImpl


class NiLexParser() : Parser {

    override fun checkRequirements(code: Code): Boolean {
        return super.checkRequirements(code)
    }

    override fun parse(code: Code): Tree<Token> {
        val tree = NodedSpecificTreeImpl<Token>()
        val tokens = (Tokens of code)!!
        val root: Token = tokens[0]
        tree.rootData = root
        tree.rootNode!!.children


        // combinations
        // remove gaps
//        val q = ArrayDeque((Tokens of code)!!.filter {
//                it.type.name !in NiLexLanguage.gaps &&
//                        it.type !is NiLexTokenizer.Kept.Opener &&
//                        it.type !is NiLexTokenizer.Kept.Closer
//        })
//        while (q.isNotEmpty()) {
//            val token = q.removeFirst()
//            when (token.type.name) {
//                "newline" -> continue
//                "<import>" -> NiLexLanguage.get(code, token, q, "string")?.also {
//                    parse(Code(File(it[0] + "." + fileExt)))
//                }
//                "<verbatim>" -> NiLexLanguage.get(code, token, q, "string")?.also {
//                    array.add(NiLexLanguage.verbatim(it[0]))
//                }
//                "<streak>" -> NiLexLanguage.get(code, token, q, "string", "<as>", "identifier")?.also {
//                    array.add(NiLexLanguage.streak(it[2], it[0]))
//                }
//                "<keep>" -> NiLexLanguage.get(code, token, q, "string", "<...>", "string", "<as>", "identifier")?.also {
//                    array.add(NiLexLanguage.kept(it[4], it[0], it[2]))
//                }
//                else -> code.issue(token, "unexpected token: $token")
//            }
//        }
        return tree
    }

//    private fun get(code: Code, initialToken: Token, q: ArrayDeque<Token>, vararg types: String): List<String>? {
//        val dataList: MutableList<String> = ArrayList()
//        var token: Token = initialToken
//        types.forEach { expectedType ->
//            while (true) {
//                val previousToken = token
//                token = q.removeFirst()
//                when (token.type.name) {
//                    "whitespace", "multiLineComment" -> {
//                        continue
//                    }
//                    expectedType -> {
//                        dataList.add(token.data)
//                        break
//                    }
//                    "newline" -> {
//                        code.issue(previousToken, "missing token, expected: $expectedType")
//                        return null
//                    }
//                    else -> {
//                        code.issue(token, "wrong token type, expected: $expectedType, got ${token.type.name}")
//                        return null
//                    }
//                }
//            }
//        }
//        return dataList
//    }
}