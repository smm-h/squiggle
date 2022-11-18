package ir.smmh.math.abstractalgebra

import ir.smmh.math.abstractalgebra.GroupLike.Properties.ASSOCIATIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.CLOSED
import ir.smmh.math.abstractalgebra.GroupLike.Properties.COMMUTATIVE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.DIVISIBLE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.IDEMPOTENT
import ir.smmh.math.abstractalgebra.GroupLike.Properties.INVERTIBLE
import ir.smmh.math.abstractalgebra.GroupLike.Properties.REGULAR
import ir.smmh.math.abstractalgebra.GroupLike.Properties.UNITAL
import ir.smmh.math.settheory.Set

/**
 * A binary operation defined on a domain
 */
class GroupLike<T>(
    val domain: Set<T>,
    val combine: Binary<T>,
    val inverse: OptionalUnary<T>? = null,
    val identity: T? = null,
    val properties: Property.Holder,
) {

    val name: String? by lazy(::generateName)
    private fun generateName(): String? {
        if (CLOSED in properties) {
            if (ASSOCIATIVE in properties) {
                if (IDEMPOTENT in properties) {
                    if (COMMUTATIVE in properties)
                        return "Semi-lattice"
                    else
                        return "Band"
                } else {
                    if (REGULAR in properties)
                        if (INVERTIBLE in properties)
                            return "Inverse semi-group"
                        else
                            return "Regular semi-group"
                    if (UNITAL in properties) {
                        if (INVERTIBLE in properties) {
                            if (COMMUTATIVE in properties)
                                return "Abelian group"
                            else
                                return "Group"
                        } else {
                            if (COMMUTATIVE in properties)
                                return "Commutative monoid"
                            else
                                return "Monoid"
                        }
                    } else
                        return "Semi-group"
                }
            } else {
                if (INVERTIBLE in properties)
                    return "Loop" // also UNITAL and DIVISIBLE
                if (DIVISIBLE in properties)
                    return "Quasi-group"
                if (UNITAL in properties)
                    return "Unital magma"
                return "Magma"
            }
        } else {
            if (ASSOCIATIVE in properties) {
                if (UNITAL in properties)
                    if (DIVISIBLE in properties)
                        return "Groupoid"
                    else
                        return "Small category"
                else
                    return "Semi-groupoid"
            }
        }
        return null
    }

    private sealed class Single : Property {
        class Nondirectional(
            override val adjective: String,
            override val noun: String,
            val testMaker: (GroupLike<Any>) -> (() -> Boolean),
        ) : Single()

        class Directional(
            override val adjective: String,
            override val noun: String,
            val testMaker: (GroupLike<Any>) -> (Pair<() -> Boolean, () -> Boolean>),
        ) : Single(), Property.Directional {
            override val left: Property.Directional.Side = Side("Left")
            override val right: Property.Directional.Side = Side("Right")

            private inner class Side(prefix: String) : Single(), Property.Directional.Side {
                override val full: Property.Directional = this@Directional
                override val adjective = prefix + "-" + full.adjective
                override val noun = prefix + "-" + full.noun
            }
        }

        interface Test {
            fun <T> test(structure: GroupLike<T>): Boolean
        }
    }

    object Properties {
        val CLOSED: Property = Single.Nondirectional("Closed", "Closure") {
            val f = it.combine
            {
                val (a, b) = it.domain.pickTwo()
                f(a, b) in it.domain
            }
        }
        val ASSOCIATIVE: Property = Single.Nondirectional("Associative", "Associativity") {
            val f = it.combine
            {
                val (a, b, c) = it.domain.pickThree()
                f(f(a, b), c) == f(a, f(b, c))
            }
        }
        val COMMUTATIVE: Property = Single.Nondirectional("Commutative", "Commutativity") {
            val f = it.combine
            {
                val (a, b) = it.domain.pickTwo()
                f(a, b) == f(b, a)
            }
        }
        val UNITAL: Property = Single.Nondirectional("Unital", "Unitallity") {
            if (it.identity == null) {
                { false }
            } else {
                { true }
            }
        }
        val DIVISIBLE: Property = Single.Nondirectional("Divisible", "Divisibility") {
            val f = it.combine
            {
                val (a, b) = it.domain.pickTwo()
                val (x, y) = it.domain.pickTwo()
                f(a, x) == b && f(y, a) == b // TODO
            }
        }
        val REGULAR: Property = Single.Nondirectional("Regular", "Regularity") {
            val f = it.combine
            {
                val (a, z) = it.domain.pickTwo()
                f(f(a, z), a) == a
            }
        }
        val INVERTIBLE: Property = Single.Nondirectional("Invertible", "Invertibility") {
            val f = it.combine
            if (it.identity == null) {
                { false }
            } else {
                {
                    val (a, z) = it.domain.pickTwo()
                    it.identity == f(a, z) && it.identity == f(z, a)
                }
            }
        }
        val IDEMPOTENT: Property = Single.Nondirectional("Idempotent", "Idempotency") {
            val f = it.combine
            {
                val a = it.domain.pick()
                f(a, a) == a
            }
        }
        val CANCELLATIVE: Property.Directional = Single.Directional("Cancellative", "Cancellativity") {
            val f = it.combine
            val L = {
                val (a, b, c) = it.domain.pickThree()
                if (f(c, a) == f(c, b)) a == b else true// TODO
            }
            val R = {
                val (a, b, c) = it.domain.pickThree()
                if (f(a, c) == f(b, c)) a == b else true
            }
            L to R
        }
        val COMPLEMENTIVE: Property = Single.Nondirectional("Complementive", "Complementivity") {
            val f = it.combine
            val i = it.identity
            {
                val (a, b) = it.domain.pickTwo()
                f(a, b) == i // TODO
            }
        }
        val MONOTONIC: Property = Single.Nondirectional("Monotonic", "Monotonicity") {
            val f = it.combine
            val d = it.domain
            if (d is Set.Ordered) {
                val o = d.partialOrder
                {
                    val (a, b, c) = d.pickThree()
                    !(o(a, b) xor o(f(a, c), f(b, c)))
                }
            } else {
                { false }
            }
        }
    }

    companion object {
        val AbelianGroupProperties = Property.Holder(
            CLOSED,
            UNITAL,
            ASSOCIATIVE,
            INVERTIBLE,
            COMMUTATIVE,
        )
    }
}