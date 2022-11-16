package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.Structures

// class MatrixMultiplicationTest

fun main() {
    val r = Structures.Integer32Ring
    val a = Matrix.of(4, 3, r, 1, 0, 1, 2, 1, 1, 0, 1, 1, 1, 1, 2)
    val b = Matrix.of(3, 3, r, 1, 2, 1, 2, 3, 1, 4, 2, 2)
    val c = Matrix.of(4, 3, r, 5, 4, 3, 8, 9, 5, 6, 5, 3, 11, 9, 6)
    println(a * b)
    println(c)
}