package ir.smmh.math.abstractalgebra

import ir.smmh.math.abstractalgebra.GroupLike.Properties.ASSOCIATIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.CLOSED
import ir.smmh.math.abstractalgebra.GroupLike.Properties.COMMUTATIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.COMPLEMENTIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.IDEMPOTENT
import ir.smmh.math.abstractalgebra.GroupLike.Properties.INVERTIBLE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.UNITAL
import ir.smmh.math.abstractalgebra.RingLike.Properties.ABSORPTIVE
import ir.smmh.math.abstractalgebra.RingLike.Properties.DISTRIBUTIVE
import ir.smmh.math.settheory.Set

/**
 * Two binary operations defined on the same domain: addition and multiplication
 */
class RingLike<T>(
    val domain: Set<T>,
    val addition: GroupLike<T>,
    val multiplication: GroupLike<T>,
    val subtraction: Binary<T>? = null,
    val division: Binary<T>? = null,
    val additionOverMultiplication: Property.Holder,
    val multiplicationOverAddition: Property.Holder,
) {
    val name: String? by lazy(::generateName)
    private fun generateName(): String? {
        if (CLOSED in addition.properties && ASSOCIATIVE in addition.properties) {
            if (UNITAL in addition.properties && CLOSED in multiplication.properties) {
                if (
                    INVERTIBLE in addition.properties &&
                    COMMUTATIVE in addition.properties &&
                    DISTRIBUTIVE in multiplicationOverAddition
                ) {
                    if (UNITAL in multiplication.properties) {
                        if (INVERTIBLE in multiplication.properties && COMMUTATIVE in multiplication.properties)
                            return "Field"
                        if (COMMUTATIVE in multiplication.properties)
                            return "Commutative ring"
                        if (INVERTIBLE in multiplication.properties)
                            return "Division ring"
                        else
                            return "Ring"
                    } else {
                        if (COMMUTATIVE in multiplication.properties)
                            return "Integral domain"
                        else
                            return "Rng"
                    }
                }
                if (INVERTIBLE in addition.properties) {
                    if (ASSOCIATIVE in multiplication.properties) {
                        if (DISTRIBUTIVE in multiplicationOverAddition)
                            return "Near-ring"
                        if (DISTRIBUTIVE.left in multiplicationOverAddition)
                            return "Left near-ring"
                        if (DISTRIBUTIVE.right in multiplicationOverAddition)
                            return "Right near-ring"
                    }
                }
                if (
                    COMMUTATIVE in addition.properties &&
                    UNITAL in multiplication.properties &&
                    DISTRIBUTIVE in multiplicationOverAddition
                )
                    return "Semi-ring"
            }
            if (
                COMMUTATIVE in addition.properties &&
                IDEMPOTENT in addition.properties &&
                ABSORPTIVE in additionOverMultiplication &&
                CLOSED in multiplication.properties &&
                ASSOCIATIVE in multiplication.properties &&
                COMMUTATIVE in multiplication.properties &&
                IDEMPOTENT in multiplication.properties &&
                ABSORPTIVE in multiplicationOverAddition
            ) {
                if (UNITAL in addition.properties && UNITAL in multiplication.properties) {
                    if (COMPLEMENTIVE in addition.properties && COMPLEMENTIVE in multiplication.properties)
                        return "Complemented lattice"
                    else
                        return "Bounded lattice"
                } else
                    return "Lattice"
            }
        }
        return null
    }

    val additiveName: String? by lazy {
        val n = addition.name
        if (n == null) null else "Additive $n"
    }
    val multiplicativeName: String? by lazy {
        val n = multiplication.name
        if (n == null) null else "Multiplicative $n"
    }

    fun add(a: T, b: T): T =
        addition.combine(a, b)

    fun negate(a: T): T =
        addition.inverse!!(a)!!

    fun negatable(a: T): Boolean {
        val neg = addition.inverse
        return neg != null && neg(a) != null
    }

    fun multiply(a: T, b: T): T =
        multiplication.combine(a, b)

    fun invert(a: T): T =
        multiplication.inverse!!(a)!!

    fun invertible(a: T): Boolean {
        val inv = multiplication.inverse
        return inv != null && inv(a) != null
    }

    fun subtract(a: T, b: T): T =
        if (subtraction != null) subtraction.invoke(a, b) else add(a, negate(b))

    fun divide(a: T, b: T): T =
        if (division != null) division.invoke(a, b) else multiply(a, invert(b))

    fun remainder(a: T, b: T): T =
        subtract(a, multiply(divide(a, b), b))

    private sealed class Double : Property {
        class Nondirectional(
            override val adjective: String,
            override val noun: String,
            val testMaker: (RingLike<Any>) -> (() -> Boolean),
        ) : Double()

        class Directional(
            override val adjective: String,
            override val noun: String,
            val testMaker: (RingLike<Any>) -> (Pair<() -> Boolean, () -> Boolean>),
        ) : Double(), Property.Directional {
            override val left: Property.Directional.Side = Side("Left")
            override val right: Property.Directional.Side = Side("Right")

            private inner class Side(prefix: String) : Double(), Property.Directional.Side {
                override val full: Property.Directional = this@Directional
                override val adjective = prefix + "-" + full.adjective
                override val noun = prefix + "-" + full.noun
            }
        }

        interface Test {
            fun <T> test(structure: RingLike<T>): Boolean
        }
    }

    object Properties {
        val DISTRIBUTIVE: Property.Directional = Double.Directional("Ditributive", "Distributivity") {
            val A = it.addition.combine
            val M = it.multiplication.combine
            val L = {
                val (a, b, c) = it.domain.pickThree()
                M(a, A(b, c)) == A(M(a, b), M(a, c))
            }
            val R = {
                val (a, b, c) = it.domain.pickThree()
                M(A(a, b), c) == A(M(a, c), M(b, c))
            }
            L to R
        }
        val ABSORPTIVE: Property = Double.Nondirectional("Absorptive", "Absorptivity") {
            val A = it.addition.combine
            val M = it.multiplication.combine
            {
                val (a, b) = it.domain.pickTwo()
                M(a, A(a, b)) == a && A(a, M(a, b)) == a
            }
        }
    }

    companion object {
        fun <T> ring(
            domain: Set<T>,
            additiveCombine: Binary<T>,
            additiveInverse: OptionalUnary<T>,
            additiveIdentity: T,
            multiplicativeCombine: Binary<T>,
            multiplicativeInverse: OptionalUnary<T>? = null,
            multiplicativeIdentity: T,
            subtraction: Binary<T>? = null,
            division: Binary<T>? = null,
        ): RingLike<T> = RingLike(
            domain,
            GroupLike(
                domain,
                additiveCombine,
                additiveInverse,
                additiveIdentity,
                GroupLike.AbelianGroupProperties,
            ),
            GroupLike(
                domain,
                multiplicativeCombine,
                multiplicativeInverse,
                multiplicativeIdentity,
                Property.Holder(
                    CLOSED,
                    UNITAL,
                )
            ),
            subtraction,
            division,
            Property.Holder.empty,
            Property.Holder(DISTRIBUTIVE),
        )

        fun <T> field(
            domain: Set<T>,
            additiveCombine: Binary<T>,
            additiveInverse: OptionalUnary<T>,
            additiveIdentity: T,
            multiplicativeCombine: Binary<T>,
            multiplicativeInverse: OptionalUnary<T>,
            multiplicativeIdentity: T,
            subtraction: Binary<T>? = null,
            division: Binary<T>? = null,
        ): RingLike<T> = RingLike(
            domain,
            GroupLike(
                domain,
                additiveCombine,
                additiveInverse,
                additiveIdentity,
                GroupLike.AbelianGroupProperties,
            ),
            GroupLike(
                domain,
                multiplicativeCombine,
                multiplicativeInverse,
                multiplicativeIdentity,
                Property.Holder(
                    CLOSED,
                    UNITAL,
                    INVERTIBLE,
                    COMMUTATIVE,
                ),
            ),
            subtraction,
            division,
            Property.Holder.empty,
            Property.Holder(DISTRIBUTIVE),
        )
    }
}