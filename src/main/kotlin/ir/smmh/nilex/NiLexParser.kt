package ir.smmh.nilex

import ir.smmh.lingu.Code
import ir.smmh.lingu.Parser
import ir.smmh.lingu.Token
import ir.smmh.tree.Tree
import ir.smmh.tree.impl.NodedSpecificTreeImpl


class NiLexParser() : Parser {
    override fun parse(code: Code): Tree<Token> {
        val tree = NodedSpecificTreeImpl<Token>()

        return tree
    }
}