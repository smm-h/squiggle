package ir.smmh.math.numbers

import ir.smmh.math.numbers.BuiltinNumberType.*
import ir.smmh.util.MathUtil


object Numbers {

    val ZERO = IntNatural(0)
    val ONE = IntNatural(1)
    val TWO = IntNatural(2)

    /**
     * 0, 1, 2, ...
     */
    sealed interface Natural : Integer {
        override fun unaryPlus(): Natural = this
        operator fun plus(that: Natural): Natural = LongNatural(approximateAsLong() + that.approximateAsLong())
        operator fun times(that: Natural): Natural = LongNatural(approximateAsLong() * that.approximateAsLong())

        fun quotient(that: Natural): Natural = LongNatural(this.approximateAsLong() / that.approximateAsLong())
        operator fun rem(that: Natural): Natural = LongNatural(this.approximateAsLong() % that.approximateAsLong())
        fun quotientWithRemainder(that: Natural): Pair<Natural, Natural> {
            val a = this.approximateAsLong()
            val b = that.approximateAsLong()
            return LongNatural(a / b) to LongNatural(a % b)
        }

        override val isNegative: Boolean get() = false
        override val absolute: Natural get() = this
        override val squared: Natural get() = this * this
        override val squareRoot: Real get() = DoubleReal(Math.sqrt(approximateAsDouble()))

        override fun isNatural() = true
        override fun asNatural() = this
    }

    sealed interface Prime : Natural
    sealed interface Composite : Natural

    /**
     * [Natural]s and their negatives
     */
    sealed interface Integer : Rational {

        fun approximateAsLong(): Long

        override val debugText: String get() = approximateAsDouble().toString()
        override val tex: String get() = "{$debugText}"

        override val wholePart: Integer get() = this
        override val fractionalPart: Real get() = ZERO

        override val numerator: Integer get() = this
        override val denominator: Integer get() = ONE

        override fun unaryPlus(): Integer = this
        override fun unaryMinus(): Integer = LongInteger(-this.approximateAsLong())
        operator fun plus(that: Integer): Integer = LongInteger(this.approximateAsLong() + that.approximateAsLong())
        operator fun times(that: Integer): Integer = LongInteger(this.approximateAsLong() * that.approximateAsLong())
        operator fun minus(that: Integer): Integer = this + (-that)
        operator fun div(that: Integer): Rational = Rational.RR(this, that)

        // Euclidean division
        fun quotient(that: Integer): Integer = LongInteger(this.approximateAsLong() / that.approximateAsLong())
        operator fun rem(that: Integer): Integer = LongInteger(this.approximateAsLong() % that.approximateAsLong())
        fun quotientWithRemainder(that: Integer): Pair<Integer, Integer> {
            val a = this.approximateAsLong()
            val b = that.approximateAsLong()
            return LongInteger(a / b) to LongInteger(a % b)
        }

        override val isNegative: Boolean get() = numerator.isNegative xor denominator.isNegative
        override val absoluteSquared: Natural get() = squared
        override val absolute: Natural get() = LongNatural(Math.abs(this.approximateAsLong()))
        override val squared: Natural get() = LongNatural(MathUtil.sqr(this.approximateAsLong()))
        override val reciprocal: Rational get() = Numbers.Rational.RR(ONE, this)

        override fun isInteger() = true
        override fun asInteger() = this

        fun isNatural(): Boolean = !isNegative
        fun asNatural(): Natural = LongNatural(this.approximateAsLong())
    }

    /**
     * Two [Integer]s divided by each other
     */
    sealed interface Rational : Real {

        override fun approximateAsDouble(): Double = numerator.approximateAsDouble() / denominator.approximateAsLong()

        val numerator: Integer
        val denominator: Integer

        override val debugText get() = "${numerator.debugText}/${denominator.debugText}"
        override val tex get() = if (denominator == ONE) numerator.tex else "{${numerator.tex}\\over${denominator.tex}}"

        override fun unaryPlus(): Rational =
            this

        operator fun plus(that: Rational): Rational =
            RR(numerator * that.denominator + that.numerator * denominator, denominator * that.denominator)

        override fun unaryMinus(): Rational =
            RR(-numerator, denominator)

        operator fun minus(that: Rational): Rational =
            this + (-that)

        operator fun times(that: Rational): Rational =
            RR(numerator * that.numerator, denominator * that.denominator)

        override val reciprocal: Rational
            get() = RR(denominator, numerator)

        operator fun div(that: Rational): Rational =
            this * that.reciprocal

        override val squared: Rational get() = this * this
        override val squareRoot: Complex get() = numerator.squareRoot / denominator.squareRoot

        override val isNegative: Boolean get() = numerator.isNegative xor denominator.isNegative
        override val absolute: Rational get() = if (isNegative) -this else this
        override val absoluteSquared: Rational get() = squared

        override fun isRational() = true
        override fun asRational() = this
        override fun isIrrational() = false
        override fun asIrrational() = null

        fun isInteger(): Boolean = fractionalPart == ZERO
        fun asInteger(): Integer? = if (isInteger()) wholePart else null

        class RR(
            override val numerator: Numbers.Integer,
            override val denominator: Numbers.Integer,
        ) : Numbers.Rational
    }

    /**
     * [Rational] numbers whose decimal representation does not terminate
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
     * [Rational] numbers and [Irrational] numbers put together
     */
    sealed interface Real : Complex {

        fun approximateAsDouble(): Double

        override val debugText: String get() = approximateAsDouble().toString()
        override val tex: String get() = "{$debugText}"

        val wholePart: Integer get() = IntInteger(Math.floor(approximateAsDouble()).toInt())
        val fractionalPart: Real get() = IntInteger(approximateAsDouble().let { (it - Math.floor(it)).toInt() })

        override val realPart: Real get() = this
        override val imaginaryPart: Real get() = ZERO

        override fun unaryPlus(): Real = this
        operator fun plus(that: Real): Real = DoubleReal(approximateAsDouble() + that.approximateAsDouble())
        override fun unaryMinus(): Real = DoubleReal(-approximateAsDouble())
        operator fun minus(that: Real): Real = this + (-that)

        operator fun times(that: Real): Real = DoubleReal(approximateAsDouble() * that.approximateAsDouble())
        override val reciprocal: Real get() = DoubleReal(1 / approximateAsDouble())
        operator fun div(that: Real): Real = this * that.reciprocal

        val squared: Real get() = this * this
        val squareRoot: Complex
            get() =
                if (isNegative) Complex.RR(ZERO, absolute.squareRoot.asReal()!!)
                else DoubleReal(Math.sqrt(approximateAsDouble()))

        val isNegative: Boolean get() = ZERO > this

        operator fun compareTo(that: Real): Int = approximateAsDouble().compareTo(that.approximateAsDouble())

        override val absolute: Real get() = if (isNegative) -this else this
        override val absoluteSquared: Real get() = squared

        override fun isReal() = true
        override fun asReal() = this

        fun isRational(): Boolean = true
        fun asRational(): Numbers.Rational? {
            val d = longDenominator
            val n = (approximateAsDouble() * d).toLong()
            val gcd = MathUtil.gcd(n, d)
            return Numbers.Rational.RR(LongInteger(n / gcd), LongInteger(d / gcd))
        }

        fun isIrrational(): Boolean = !isRational()
        fun asIrrational(): Numbers.Irrational? = null
    }

    /**
     * 20!
     */
    private const val longDenominator = 2432902008176640000L // 1000000000000000L
}