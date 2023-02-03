package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.IntegerRing
import ir.smmh.math.abstractalgebra.RealField
import ir.smmh.math.numbers.Numbers
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MatrixTest {

    val i = IntegerRing
    val r = RealField

    //val i2r: (Matrix<Int>) -> Matrix<Float> = { it.convert(r) { it.toFloat() } }
    //val r2i: (Matrix<Float>) -> Matrix<Int> = { it.convert(i) { it.toInt() } }

    @Test
    fun testMultiplication() {
        val a = Matrix.of(4, 3, i, listOf(1, 0, 1, 2, 1, 1, 0, 1, 1, 1, 1, 2).map(Numbers.Integer::of))
        val b = Matrix.of(3, 3, i, listOf(1, 2, 1, 2, 3, 1, 4, 2, 2).map(Numbers.Integer::of))
        val c = Matrix.of(4, 3, i, listOf(5, 4, 3, 8, 9, 5, 6, 5, 3, 11, 9, 6).map(Numbers.Integer::of))
        assertEquals(c, a * b)
    }

    @Test
    fun testDeterminant2x2() {
        assertEquals(
            Numbers.Integer.of(-19),
            Matrix.of(2, 2, i, listOf(3, 7, 1, -4).map(Numbers.Integer::of)).determinant
        )
    }

    @Test
    fun testDeterminant3x3() {
        assertEquals(
            Numbers.Integer.of(49),
            Matrix.of(3, 3, i, listOf(2, -3, 1, 2, 0, -1, 1, 4, 5).map(Numbers.Integer::of)).determinant
        )
    }
}