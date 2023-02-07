package ir.smmh.math.symbolic.conventions

object PrimeNumbersSetNamingConvention : Convention {
    override val description = "Set of all prime numbers"
    override val options = 2
    override fun optionDescription(index: Int): String = when (index) {
        0 -> "Blackboard P"
        1 -> "Boldface P"
        else -> ""
    }
}