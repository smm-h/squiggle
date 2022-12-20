package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.Structures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MatrixTest {

    val i = Structures.Integer32Ring
    val r = Structures.FloatingPoint32Field

    val i2r: (Matrix<Int>) -> Matrix<Float> = { it.convert(r) { it.toFloat() } }
    val r2i: (Matrix<Float>) -> Matrix<Int> = { it.convert(i) { it.toInt() } }

    @Test
    fun testMultiplication() {
        val a = Matrix.of(4, 3, i, 1, 0, 1, 2, 1, 1, 0, 1, 1, 1, 1, 2)
        val b = Matrix.of(3, 3, i, 1, 2, 1, 2, 3, 1, 4, 2, 2)
        val c = Matrix.of(4, 3, i, 5, 4, 3, 8, 9, 5, 6, 5, 3, 11, 9, 6)
        assertEquals(c, a * b)
    }

    @Test
    fun testDeterminant2x2() {
        assertEquals(-19, Matrix.of(2, 2, i, 3, 7, 1, -4).determinant)
    }

    @Test
    fun testDeterminant3x3() {
        assertEquals(49, Matrix.of(3, 3, i, 2, -3, 1, 2, 0, -1, 1, 4, 5).determinant)
    }
}