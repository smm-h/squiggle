package ir.smmh.math.numbers

import ir.smmh.math.InfinitelyIterable
import ir.smmh.math.MathematicalCollection
import ir.smmh.math.logic.Logical
import ir.smmh.math.numbers.Numbers.Integer
import ir.smmh.math.numbers.Numbers.Natural
import ir.smmh.math.numbers.Numbers.Rational
import ir.smmh.math.numbers.Numbers.Real
import ir.smmh.math.settheory.AbstractSet
import kotlin.random.Random
import ir.smmh.math.MathematicalObject as M

sealed class UniversalNumberSet<T : Real> : AbstractSet<T>() {

    override fun contains(it: T) = Logical.True
    override fun isEmpty() = Logical.False

    class N(val pickerSize: Int) : UniversalNumberSet<Natural>() {
        override val debugText = "N"
        override val tex = "\\mathbb{N}"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is N)

        override val overElements = InfinitelyIterable<Natural> {
            var i = 0
            InfinitelyIterable.Iterator<Natural> {
                Natural.of(i++)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Natural> {
            Natural.of(random.nextInt(pickerSize))
        }
    }

    class Z(val pickerSize: Int) : UniversalNumberSet<Integer>() {
        override val debugText = "Z"
        override val tex = "\\mathbb{Z}"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is Z)

        override val overElements = InfinitelyIterable<Integer> {
            var i = 0
            var n = false
            InfinitelyIterable.Iterator<Integer> {
                i = -i
                n = !n
                if (n) i++
                Integer.of(i)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Integer> {
            Integer.of(random.nextInt(pickerSize * 2) - pickerSize)
        }
    }

    class Q(val pickerSize: Int) : UniversalNumberSet<Rational>() {
        override val debugText = "Q"
        override val tex = "\\mathbb{Q}"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is Q)

        override val overElements = InfinitelyIterable<Rational> {
            var i = 0
            var n = false
            InfinitelyIterable.Iterator<Rational> {
                i = -i
                n = !n
                if (n) i++
                Rational.of(i, 6)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Rational> {
            Rational.of(random.nextInt(100), random.nextInt(100) + 1)
        }
    }

    class R(val pickerSize: Int) : UniversalNumberSet<Real>() {
        override val debugText = "R"
        override val tex = "\\mathbb{R}"
        override fun isNonReferentiallyEqualTo(that: M) = Logical.of(that is R)

        private val increment = 0.1

        override val overElements = InfinitelyIterable<Real> {
            var i = 0.0
            var n = false
            InfinitelyIterable.Iterator<Real> {
                i = -i
                n = !n
                if (n) i += increment
                Real.of(i)
            }
        }

        override fun getPicker(random: Random) = MathematicalCollection.Picker<Real> {
            Real.of((random.nextDouble() * 2 - 1) * pickerSize)
        }
    }
}