package ir.smmh.math.settheory

import ir.smmh.math.symbolic.TeXable

/**
 * [On Wikipedia](https://en.wikipedia.org/wiki/Cardinal_number)
 */
interface CardinalNumber : TeXable {

    /**
     * [On Wikipedia](https://en.wikipedia.org/wiki/Aleph_number#Aleph-nought)
     */
    object AlephNought : CardinalNumber {
        override val tex: String = "\\aleph_0"
    }

    /**
     * [On Wikipedia](https://en.wikipedia.org/wiki/Cardinality_of_the_continuum)
     */
    object CardinalityOfTheContinuum : CardinalNumber {
        override val tex: String = "\\mathfrak{c}"
    }

    /**
     * [On Wikipedia](https://en.wikipedia.org/wiki/Absolute_Infinite)
     */
    object AbsoluteInfinite : CardinalNumber {
        override val tex: String = "\\Omega"
    }
}