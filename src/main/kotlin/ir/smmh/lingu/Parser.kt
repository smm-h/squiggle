package ir.smmh.lingu

fun interface Parser : Code.Process {
    fun parse(code: Code): List<Token>

    override fun invoke(code: Code) {
        code[syntaxTree] = parse(code)
    }

    companion object {
        val syntaxTree = Code.Aspect<List<Token>>("syntax-tree")
    }
}