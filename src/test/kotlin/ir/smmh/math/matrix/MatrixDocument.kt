package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.IntegerRing
import ir.smmh.math.abstractalgebra.RealField
import ir.smmh.math.numbers.Numbers
import ir.smmh.math.numbers.Numbers.ONE
import ir.smmh.math.numbers.Numbers.ZERO

fun main() {

    val z = IntegerRing
    val r = RealField

    val i2r: (Matrix<Numbers.Integer>) -> Matrix<Numbers.Real> =
        { it.convert(r) { Numbers.Real.of(it.approximateAsDouble()) } }

    val n = 5
    println("n = $n\n")

    val mi: Matrix<Numbers.Integer> = Matrix.identity(n).convert(z) { x -> if (x.toBoolean()) ONE else ZERO }
    println("Identity matrix:\n$mi\n")

    val multiplicationTable: (Int, Int) -> Numbers.Integer =
        { i, j -> Numbers.Integer.of((i + 1) * (j + 1)) }

    val mt: Matrix<Numbers.Integer> = FunctionMatrix.Unmemoized(n, n, z, multiplicationTable)
    println("Multiplication table:\n$mt\n")

    val mr: Matrix<Numbers.Integer> = MapMatrix(n, n, z).setAll(Matrix.getRowMajor(n))
    println("Row-major indices:\n$mr\n")

    val mc: Matrix<Numbers.Integer> = MapMatrix(n, n, z).setAll(Matrix.getColumnMajor(n))
    println("Column-major indices:\n$mc\n")

    val dr = mr - mt
    println("Row-major minus multiplication:\n$dr\n")

    val dc = mc - mt
    println("Column-major minus multiplication:\n$dc\n")

    val sum = dr + dc
    println("Their sum:\n$sum\n")

    val dif = dr - dc
    println("Their difference:\n$dif\n")

    val k = Numbers.Real.of(2.0 * (n - 1))
    val mk = UniformMatrix(n, n, r, k)

//    val sumn = (i2r(sum) - mk) / k
//    println("Their sum minus uniform(k) and divided by k:\n$sumn\n")
//
//    val difn = i2r(dif) / (k * TWO)
//    println("Their difference divided by 2k:\n$difn\n")
}