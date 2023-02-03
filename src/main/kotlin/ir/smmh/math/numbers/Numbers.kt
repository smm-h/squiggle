package ir.smmh.math.numbers

import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Logical
import ir.smmh.util.MathUtil


object Numbers {

    val ZERO = Natural.of(0)
    val ONE = Natural.of(1)
    val TWO = Natural.of(2)

    /**
     * 0, 1, 2, ...
     */
    sealed interface Natural : Integer {
        override fun unaryPlus(): Natural = this
        operator fun plus(that: Natural): Natural = of(approximateAsLong() + that.approximateAsLong())
        operator fun times(that: Natural): Natural = of(approximateAsLong() * that.approximateAsLong())

        fun quotient(that: Natural): Natural = of(this.approximateAsLong() / that.approximateAsLong())
        operator fun rem(that: Natural): Natural = of(this.approximateAsLong() % that.approximateAsLong())
        fun quotientWithRemainder(that: Natural): Pair<Natural, Natural> {
            val a = this.approximateAsLong()
            val b = that.approximateAsLong()
            return of(a / b) to of(a % b)
        }

        override val isNegative: Boolean get() = false
        override val absolute: Natural get() = this
        override val squared: Natural get() = this * this
        override val squareRoot: Real get() = Real.of(Math.sqrt(approximateAsDouble()))

        override fun isNatural() = true
        override fun asNatural() = this

        @JvmInline
        private value class IntNatural(val value: Int) : Natural {
            override fun approximateAsLong() = value.toLong()
            override fun toString() = value.toString()
        }

        @JvmInline
        private value class LongNatural(val value: Long) : Natural {
            override fun approximateAsLong() = value
            override fun toString() = value.toString()
        }

        companion object {
            fun of(value: Int): Natural = IntNatural(value)
            fun of(value: Long): Natural = LongNatural(value)
        }
    }

    sealed interface Prime : Natural
    sealed interface Composite : Natural

    /**
     * A [Natural] number or its negative
     */
    sealed interface Integer : Rational {

        fun approximateAsLong(): Long
        override fun approximateAsDouble() = approximateAsLong().toDouble()

        override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Logical.of(
            that is Quaternion &&
                    approximateAsLong() == that.asComplex()?.asReal()?.asRational()?.asInteger()?.approximateAsLong()
        )

        override val debugText: String get() = approximateAsLong().toString()
        override val tex: String get() = "{$debugText}"

        override val wholePart: Integer get() = this
        override val fractionalPart: Real get() = ZERO

        override val numerator: Integer get() = this
        override val denominator: Integer get() = ONE

        override fun unaryPlus(): Integer = this
        operator fun minus(that: Integer): Integer = this + (-that)
        operator fun div(that: Integer): Rational = this * (that.reciprocal)
        override fun unaryMinus(): Integer = of(-this.approximateAsLong())
        operator fun plus(that: Integer): Integer = of(this.approximateAsLong() + that.approximateAsLong())
        operator fun times(that: Integer): Integer = of(this.approximateAsLong() * that.approximateAsLong())

        fun quotient(that: Integer): Integer = Integer.of(this.approximateAsLong() / that.approximateAsLong())
        operator fun rem(that: Integer): Integer = Integer.of(this.approximateAsLong() % that.approximateAsLong())
        fun quotientWithRemainder(that: Integer): Pair<Integer, Integer> {
            val a = this.approximateAsLong()
            val b = that.approximateAsLong()
            return Integer.of(a / b) to Integer.of(a % b)
        }

        override val isNegative: Boolean
        override val absoluteSquared: Natural get() = squared
        override val absolute: Natural get() = Natural.of(Math.abs(this.approximateAsLong()))
        override val squared: Natural get() = Natural.of(MathUtil.sqr(this.approximateAsLong()))
        override val reciprocal: Rational get() = Rational.of(ONE, this)

        override fun isInteger() = true
        override fun asInteger() = this

        fun isNatural(): Boolean = this is Natural || !isNegative
        fun asNatural(): Natural? =
            if (this is Natural) this
            else if (isNatural()) Natural.of(this.approximateAsLong())
            else null

        @JvmInline
        private value class IntInteger(val value: Int) : Integer {
            override fun approximateAsLong() = value.toLong()
            override val isNegative: Boolean get() = value < 0
            override fun toString() = value.toString()
        }

        @JvmInline
        private value class LongInteger(val value: Long) : Integer {
            override fun approximateAsLong() = value
            override val isNegative: Boolean get() = value < 0
            override fun toString() = value.toString()
        }

        companion object {
            fun of(value: Int): Integer =
                if (value >= 0) Natural.of(value)
                else IntInteger(value)

            fun of(value: Long): Integer =
                if (value >= 0) Natural.of(value)
                else LongInteger(value)
        }
    }

    /**
     * An [Integer] over another
     */
    sealed interface Rational : Real {

        override fun approximateAsDouble(): Double = numerator.approximateAsDouble() / denominator.approximateAsLong()

        override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Logical.of(
            that is Quaternion &&
                    eq(that.asComplex()?.asReal()?.asRational())
        )

        private fun eq(that: Rational?) =
            that != null && numerator == that.numerator && denominator == that.denominator

        val numerator: Integer
        val denominator: Integer

        override val debugText
            get() = "${numerator.debugText}/${denominator.debugText}"
        override val tex
            get() = if ((denominator isEqualTo ONE).toBoolean()) numerator.tex
            else "{${numerator.tex}\\over${denominator.tex}}"

        override fun unaryPlus(): Rational = this
        operator fun minus(that: Rational): Rational = this + (-that)
        operator fun div(that: Rational): Rational = this * that.reciprocal

        override fun unaryMinus(): Rational {
            val a = this.asInteger()
            if (a != null) return -a
            val q = of(-numerator, denominator)
            val i = q.asInteger()
            return i?.asNatural() ?: i ?: q
        }

        operator fun plus(that: Rational): Rational {
            val a = this.asInteger()
            val b = that.asInteger()
            if (a != null && b != null) return a + b
            val q = of(numerator * that.denominator + that.numerator * denominator, denominator * that.denominator)
            val i = q.asInteger()
            return i?.asNatural() ?: i ?: q
        }

        override val reciprocal: Rational
            get() = of(denominator, numerator)

        operator fun times(that: Rational): Rational {
            val a = this.asInteger()
            val b = that.asInteger()
            if (a != null && b != null) return a * b
            val q = of(numerator * that.numerator, denominator * that.denominator)
            val i = q.asInteger()
            return i?.asNatural() ?: i ?: q
        }

        override val squared: Rational get() = this * this
        override val squareRoot: Complex get() = numerator.squareRoot / denominator.squareRoot

        override val isNegative: Boolean get() = numerator.isNegative xor denominator.isNegative
        override val absolute: Rational get() = if (isNegative) -this else this
        override val absoluteSquared: Rational get() = squared

        override fun isRational() = true
        override fun asRational() = this
        override fun isIrrational() = false
        override fun asIrrational() = null

        fun isInteger(): Boolean = this is Integer || (fractionalPart isEqualTo ZERO).toBoolean()
        fun asInteger(): Integer? =
            if (this is Integer) this
            else if (isInteger()) wholePart
            else null

        private class RationalImpl(
            override val numerator: Integer,
            override val denominator: Integer,
        ) : Rational {
            override fun toString() = "$numerator/$denominator"
        }

        companion object {
            fun of(numerator: Int, denominator: Int): Rational =
                if (denominator == 1) Integer.of(numerator)
                else RationalImpl(Integer.of(numerator), Integer.of(denominator))

            fun of(numerator: Long, denominator: Long): Rational =
                if (denominator == 1L) Integer.of(numerator)
                else RationalImpl(Integer.of(numerator), Integer.of(denominator))

            fun of(numerator: Integer, denominator: Integer): Rational =
                if ((denominator isEqualTo ONE).toBoolean()) numerator
                else RationalImpl(numerator, denominator)
        }
    }

    /**
     * A number whose decimal representation does not terminate
     */
    sealed interface Irrational : Real {

        override val reciprocal: Irrational
        override fun unaryPlus(): Irrational = this
        override fun unaryMinus(): Irrational

        override val squared: Real get() = this * this

        override val absolute: Irrational get() = if (isNegative) -this else this
        override val absoluteSquared: Real get() = squared

        override fun isRational() = false
        override fun asRational() = null
        override fun isIrrational() = true
        override fun asIrrational() = this
    }

    sealed interface AlgebraicIrrational : Irrational
    sealed interface Transcedental : Irrational

    /**
     * Either a [Rational] number or an [Irrational] one
     */
    sealed interface Real : Complex {

        fun approximateAsDouble(): Double

        override fun isNonReferentiallyEqualTo(that: MathematicalObject) = Logical.of(
            that is Quaternion &&
                    approximateAsDouble() == that.asComplex()?.asReal()?.approximateAsDouble()
        )

        override val debugText: String get() = approximateAsDouble().toString()
        override val tex: String get() = "{$debugText}"

        val wholePart: Integer get() = Integer.of(Math.floor(approximateAsDouble()).toInt())
        val fractionalPart: Real get() = Integer.of(approximateAsDouble().let { (it - Math.floor(it)).toInt() })

        override val realPart: Real get() = this
        override val imaginaryPart: Real get() = ZERO

        override fun unaryPlus(): Real = this
        operator fun minus(that: Real): Real = this + (-that)
        operator fun div(that: Real): Real = this * that.reciprocal
        override fun unaryMinus(): Real = of(-approximateAsDouble())
        operator fun plus(that: Real): Real = of(approximateAsDouble() + that.approximateAsDouble())
        override val reciprocal: Real get() = asRational()!!.reciprocal
        operator fun times(that: Real): Real = of(approximateAsDouble() * that.approximateAsDouble())

        val squared: Real get() = this * this
        val squareRoot: Complex
            get() =
                if (isNegative)
                    Complex.of(ZERO, absolute.squareRoot.asReal()!!)
                else
                    of(Math.sqrt(approximateAsDouble()))

        val isNegative: Boolean get() = ZERO > this

        operator fun compareTo(that: Real): Int = approximateAsDouble().compareTo(that.approximateAsDouble())

        override val absolute: Real get() = if (isNegative) -this else this
        override val absoluteSquared: Real get() = squared

        override fun isReal() = true
        override fun asReal() = this

        fun isRational(): Boolean = true
        fun asRational(): Rational? {
            if (this is Rational) return this
            val d = longDenominator
            val n = (approximateAsDouble() * d).toLong()
            val gcd = MathUtil.gcd(n, d)
            return Rational.of(Integer.of(n / gcd), Integer.of(d / gcd))
        }

        fun isIrrational(): Boolean = !isRational()
        fun asIrrational(): Irrational? = null

        @JvmInline
        private value class FloatReal(val value: Float) : Real {
            override fun approximateAsDouble() = value.toDouble()
            override fun toString() = value.toString()
        }

        @JvmInline
        private value class DoubleReal(val value: Double) : Real {
            override fun approximateAsDouble() = value
            override fun toString() = value.toString()
        }

        companion object {
            fun of(value: Float): Real {
                val i = value.toInt()
                return if (i.toFloat() == value) Integer.of(i) else FloatReal(value)
            }

            fun of(value: Double): Real {
                val i = value.toLong()
                return if (i.toDouble() == value) Integer.of(i) else DoubleReal(value)
            }
        }
    }

    /**
     * 20!
     */
    private const val longDenominator = 2432902008176640000L // 1000000000000000L
}