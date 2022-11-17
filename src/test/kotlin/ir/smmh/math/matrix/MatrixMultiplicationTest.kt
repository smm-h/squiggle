package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.Structures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MatrixMultiplicationTest {

    val i = Structures.Integer32Ring
    val r = Structures.RealFPField

    val i2r: (Matrix<Int>) -> Matrix<Float> = { it.convert(r) { it.toFloat() } }
    val r2i: (Matrix<Float>) -> Matrix<Int> = { it.convert(i) { it.toInt() } }

    @Test
    fun test() {
        val a = Matrix.of(4, 3, i, 1, 0, 1, 2, 1, 1, 0, 1, 1, 1, 1, 2)
        val b = Matrix.of(3, 3, i, 1, 2, 1, 2, 3, 1, 4, 2, 2)
        val c = Matrix.of(4, 3, i, 5, 4, 3, 8, 9, 5, 6, 5, 3, 11, 9, 6)
        assertEquals(c, a * b)
    }
}