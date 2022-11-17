package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.Structures

fun main() {
    val i = Structures.Integer32Ring
    val r = Structures.RealFPField

    val i2r: (Matrix<Int>) -> Matrix<Float> = { it.convert(r) { it.toFloat() } }

    val n = 5
    println("n = $n\n")

    val mi: Matrix<Int> = Matrix.identity(n).convert(i) { x -> if (x) 1 else 0 }
    println("Identity matrix:\n$mi\n")

    val mt: Matrix<Int> = FunctionMatrix.Unmemoized(n, n, i, Matrix.multiplicationTable)
    println("Multiplication table:\n$mt\n")

    val mr: Matrix<Int> = MapMatrix(n, n, i).setAll(Matrix.rowMajor)
    println("Row-major indices:\n$mr\n")

    val mc: Matrix<Int> = MapMatrix(n, n, i).setAll(Matrix.columnMajor)
    println("Column-major indices:\n$mc\n")

    val dr = mr - mt
    println("Row-major minus multiplication:\n$dr\n")

    val dc = mc - mt
    println("Column-major minus multiplication:\n$dc\n")

    val sum = dr + dc
    println("Their sum:\n$sum\n")

    val dif = dr - dc
    println("Their difference:\n$dif\n")

    val k = 2f * (n - 1)
    val mk = UniformMatrix(n, n, r, k)

    val sumn = (i2r(sum) - mk) / k
    println("Their sum minus uniform(k) and divided by k:\n$sumn\n")

    val difn = i2r(dif) / (k * 2)
    println("Their difference divided by 2k:\n$difn\n")
}