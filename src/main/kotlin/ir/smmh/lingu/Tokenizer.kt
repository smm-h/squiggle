package ir.smmh.lingu

fun interface Tokenizer : Code.Process {
    fun tokenize(code: Code): List<Token>

    override fun invoke(code: Code) {
        code[Tokens] = tokenize(code)
    }

    companion object {
        val Tokens = Code.Aspect<List<Token>>("tokens")
    }
}