package ir.smmh.lingu

import ir.smmh.tree.Tree

fun interface Parser : Code.Process {
    fun parse(code: Code): Tree<Token>

    override fun invoke(code: Code) {
        code[syntaxTree] = parse(code)
    }

    companion object {
        val syntaxTree = Code.Aspect<Tree<Token>>("syntax-tree")
    }
}