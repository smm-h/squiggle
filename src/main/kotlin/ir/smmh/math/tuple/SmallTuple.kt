package ir.smmh.math.tuple

import ir.smmh.math.MathematicalObject

@Suppress("DuplicatedCode")
sealed class SmallTuple : Tuple.Finitary {

    companion object {
        infix fun <T1 : MathematicalObject, T2 : MathematicalObject> T1.r(that: T2) = Couple<T1, T2>(this, that)
        infix fun <T : MathematicalObject> T.ru(that: T) = Uniform.Couple<T>(this, that)
    }

    override val debugText by lazy(::toString)

    data class Uniple<
            T1 : MathematicalObject,
            >(
        override val singleton: T1
    ) : SmallTuple(), Tuple.Unary.Specific<T1>

    data class Couple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            >(
        override val first: T1,
        override val second: T2,
    ) : SmallTuple(), Tuple.Binary.Specific<T1, T2>

    data class Triple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            >(
        override val first: T1,
        override val second: T2,
        override val third: T3,
    ) : SmallTuple(), Tuple.Ternary.Specific<T1, T2, T3>

    data class Quadruple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
    ) : SmallTuple() {
        override val length: Int get() = 4
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            else -> throw IndexOutOfBoundsException()
        }
    }

    data class Quintuple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            T5 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5,
    ) : SmallTuple() {
        override val length: Int get() = 5
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            else -> throw IndexOutOfBoundsException()
        }
    }

    data class Sextuple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            T5 : MathematicalObject,
            T6 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5,
        val sixth: T6,
    ) : SmallTuple() {
        override val length: Int get() = 6
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            5 -> sixth
            else -> throw IndexOutOfBoundsException()
        }
    }

    data class Septuple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            T5 : MathematicalObject,
            T6 : MathematicalObject,
            T7 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5,
        val sixth: T6,
        val seventh: T7,
    ) : SmallTuple() {
        override val length: Int get() = 7
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            5 -> sixth
            6 -> seventh
            else -> throw IndexOutOfBoundsException()
        }
    }

    data class Octuple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            T5 : MathematicalObject,
            T6 : MathematicalObject,
            T7 : MathematicalObject,
            T8 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5,
        val sixth: T6,
        val seventh: T7,
        val eighth: T8,
    ) : SmallTuple() {
        override val length: Int get() = 8
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            5 -> sixth
            6 -> seventh
            7 -> eighth
            else -> throw IndexOutOfBoundsException()
        }
    }

    data class Nonuple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            T5 : MathematicalObject,
            T6 : MathematicalObject,
            T7 : MathematicalObject,
            T8 : MathematicalObject,
            T9 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5,
        val sixth: T6,
        val seventh: T7,
        val eighth: T8,
        val ninth: T9,
    ) : SmallTuple() {
        override val length: Int get() = 9
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            5 -> sixth
            6 -> seventh
            7 -> eighth
            8 -> ninth
            else -> throw IndexOutOfBoundsException()
        }
    }

    data class Decuple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            T5 : MathematicalObject,
            T6 : MathematicalObject,
            T7 : MathematicalObject,
            T8 : MathematicalObject,
            T9 : MathematicalObject,
            T10 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5,
        val sixth: T6,
        val seventh: T7,
        val eighth: T8,
        val ninth: T9,
        val tenth: T10,
    ) : SmallTuple() {
        override val length: Int get() = 10
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            5 -> sixth
            6 -> seventh
            7 -> eighth
            8 -> ninth
            9 -> tenth
            else -> throw IndexOutOfBoundsException()
        }
    }

    data class Undecuple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            T5 : MathematicalObject,
            T6 : MathematicalObject,
            T7 : MathematicalObject,
            T8 : MathematicalObject,
            T9 : MathematicalObject,
            T10 : MathematicalObject,
            T11 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5,
        val sixth: T6,
        val seventh: T7,
        val eighth: T8,
        val ninth: T9,
        val tenth: T10,
        val eleventh: T11,
    ) : SmallTuple() {
        override val length: Int get() = 11
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            5 -> sixth
            6 -> seventh
            7 -> eighth
            8 -> ninth
            9 -> tenth
            10 -> eleventh
            else -> throw IndexOutOfBoundsException()
        }
    }

    data class Duodecuple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            T5 : MathematicalObject,
            T6 : MathematicalObject,
            T7 : MathematicalObject,
            T8 : MathematicalObject,
            T9 : MathematicalObject,
            T10 : MathematicalObject,
            T11 : MathematicalObject,
            T12 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5,
        val sixth: T6,
        val seventh: T7,
        val eighth: T8,
        val ninth: T9,
        val tenth: T10,
        val eleventh: T11,
        val twelfth: T12,
    ) : SmallTuple() {
        override val length: Int get() = 12
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            5 -> sixth
            6 -> seventh
            7 -> eighth
            8 -> ninth
            9 -> tenth
            10 -> eleventh
            11 -> twelfth
            else -> throw IndexOutOfBoundsException()
        }
    }

    data class Tredecuple<
            T1 : MathematicalObject,
            T2 : MathematicalObject,
            T3 : MathematicalObject,
            T4 : MathematicalObject,
            T5 : MathematicalObject,
            T6 : MathematicalObject,
            T7 : MathematicalObject,
            T8 : MathematicalObject,
            T9 : MathematicalObject,
            T10 : MathematicalObject,
            T11 : MathematicalObject,
            T12 : MathematicalObject,
            T13 : MathematicalObject,
            >(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5,
        val sixth: T6,
        val seventh: T7,
        val eighth: T8,
        val ninth: T9,
        val tenth: T10,
        val eleventh: T11,
        val twelfth: T12,
        val thirteenth: T13,
    ) : SmallTuple() {
        override val length: Int get() = 13
        override fun get(index: Int): MathematicalObject = when (index) {
            0 -> first
            1 -> second
            2 -> third
            3 -> fourth
            4 -> fifth
            5 -> sixth
            6 -> seventh
            7 -> eighth
            8 -> ninth
            9 -> tenth
            10 -> eleventh
            11 -> twelfth
            12 -> thirteenth
            else -> throw IndexOutOfBoundsException()
        }
    }

    sealed class Uniform<T> : SmallTuple() {

        data class Uniple<T : MathematicalObject>(
            override val singleton: T
        ) : SmallTuple(), Tuple.Unary.Specific<T>

        data class Couple<T : MathematicalObject>(
            override val first: T,
            override val second: T,
        ) : SmallTuple(), Tuple.Binary.Uniform<T>

        data class Triple<T : MathematicalObject>(
            override val first: T,
            override val second: T,
            override val third: T,
        ) : SmallTuple(), Tuple.Ternary.Uniform<T>

        data class Quadruple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
        ) : Uniform<T>() {
            override val length: Int get() = 4
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                else -> throw IndexOutOfBoundsException()
            }
        }

        data class Quintuple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
            val fifth: T,
        ) : Uniform<T>() {
            override val length: Int get() = 5
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                4 -> fifth
                else -> throw IndexOutOfBoundsException()
            }
        }

        data class Sextuple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
            val fifth: T,
            val sixth: T,
        ) : Uniform<T>() {
            override val length: Int get() = 6
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                4 -> fifth
                5 -> sixth
                else -> throw IndexOutOfBoundsException()
            }
        }

        data class Septuple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
            val fifth: T,
            val sixth: T,
            val seventh: T,
        ) : Uniform<T>() {
            override val length: Int get() = 7
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                4 -> fifth
                5 -> sixth
                6 -> seventh
                else -> throw IndexOutOfBoundsException()
            }
        }

        data class Octuple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
            val fifth: T,
            val sixth: T,
            val seventh: T,
            val eighth: T,
        ) : Uniform<T>() {
            override val length: Int get() = 8
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                4 -> fifth
                5 -> sixth
                6 -> seventh
                7 -> eighth
                else -> throw IndexOutOfBoundsException()
            }
        }

        data class Nonuple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
            val fifth: T,
            val sixth: T,
            val seventh: T,
            val eighth: T,
            val ninth: T,
        ) : Uniform<T>() {
            override val length: Int get() = 9
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                4 -> fifth
                5 -> sixth
                6 -> seventh
                7 -> eighth
                8 -> ninth
                else -> throw IndexOutOfBoundsException()
            }
        }

        data class Decuple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
            val fifth: T,
            val sixth: T,
            val seventh: T,
            val eighth: T,
            val ninth: T,
            val tenth: T,
        ) : Uniform<T>() {
            override val length: Int get() = 10
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                4 -> fifth
                5 -> sixth
                6 -> seventh
                7 -> eighth
                8 -> ninth
                9 -> tenth
                else -> throw IndexOutOfBoundsException()
            }
        }

        data class Undecuple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
            val fifth: T,
            val sixth: T,
            val seventh: T,
            val eighth: T,
            val ninth: T,
            val tenth: T,
            val eleventh: T,
        ) : Uniform<T>() {
            override val length: Int get() = 11
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                4 -> fifth
                5 -> sixth
                6 -> seventh
                7 -> eighth
                8 -> ninth
                9 -> tenth
                10 -> eleventh
                else -> throw IndexOutOfBoundsException()
            }
        }

        data class Duodecuple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
            val fifth: T,
            val sixth: T,
            val seventh: T,
            val eighth: T,
            val ninth: T,
            val tenth: T,
            val eleventh: T,
            val twelfth: T,
        ) : Uniform<T>() {
            override val length: Int get() = 12
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                4 -> fifth
                5 -> sixth
                6 -> seventh
                7 -> eighth
                8 -> ninth
                9 -> tenth
                10 -> eleventh
                11 -> twelfth
                else -> throw IndexOutOfBoundsException()
            }
        }

        data class Tredecuple<T : MathematicalObject>(
            val first: T,
            val second: T,
            val third: T,
            val fourth: T,
            val fifth: T,
            val sixth: T,
            val seventh: T,
            val eighth: T,
            val ninth: T,
            val tenth: T,
            val eleventh: T,
            val twelfth: T,
            val thirteenth: T,
        ) : Uniform<T>() {
            override val length: Int get() = 13
            override fun get(index: Int): MathematicalObject = when (index) {
                0 -> first
                1 -> second
                2 -> third
                3 -> fourth
                4 -> fifth
                5 -> sixth
                6 -> seventh
                7 -> eighth
                8 -> ninth
                9 -> tenth
                10 -> eleventh
                11 -> twelfth
                12 -> thirteenth
                else -> throw IndexOutOfBoundsException()
            }
        }
    }
}