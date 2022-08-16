package ir.smmh.lingu

fun interface Tokenizer : Code.Process {
    fun tokenize(code: Code): List<Token>

    override fun invoke(code: Code) {
        code[tokens] = tokenize(code)
    }

    companion object {
        val tokens = Code.Aspect<List<Token>>("tokens")
    }
}