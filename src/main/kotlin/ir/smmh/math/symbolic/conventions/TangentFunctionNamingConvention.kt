package ir.smmh.math.symbolic.conventions

object TangentFunctionNamingConvention : Convention {
    override val description = "Tangential functions"
    override val options = 2
    override fun optionDescription(index: Int): String = when (index) {
        0 -> "tan/cot; used mostly in Western texts"
        1 -> "tg/ctg; used mostly in Russian texts"
        else -> ""
    }
}