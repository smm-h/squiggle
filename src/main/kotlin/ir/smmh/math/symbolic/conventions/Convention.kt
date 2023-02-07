package ir.smmh.math.symbolic.conventions

interface Convention {
    val description: String
    val options: Int
    fun optionDescription(index: Int): String
}