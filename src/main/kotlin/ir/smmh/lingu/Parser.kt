package ir.smmh.lingu

import ir.smmh.lingu.Tokenizer.Companion.Tokens
import ir.smmh.tree.Tree

fun interface Parser : Code.Process.HasRequirements {
    fun parse(code: Code): Tree<Token>

    override fun checkRequirements(code: Code): Boolean {
        return Tokens in code
    }

    override fun invoke(code: Code) {
        code[SyntaxTree] = parse(code)
    }

    companion object {
        val SyntaxTree = Code.Aspect<Tree<Token>>("syntax-tree")
    }
}