package ir.smmh.lingu

fun interface Tokenizer : Code.Process {
    fun tokenize(code: Code): List<Token>

    override fun invoke(code: Code) {
        code[tokens] = tokenize(code)
    }

    companion object {
        val tokens = Code.Aspect<List<Token>>("tokens")
    }

    abstract class Named(name: String) : Tokenizer, Code.Process.Named(name) {
        override fun invoke(code: Code) = super.invoke(code) // TODO report Kotlin's stupid bug
    }
}