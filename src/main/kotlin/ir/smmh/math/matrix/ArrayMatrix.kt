package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.math.abstractalgebra.Structures
import ir.smmh.nile.Mut

class ArrayMatrix(
    override val width: Int,
    override val height: Int,
    override val mut: Mut = Mut(),
    initialValueFunction: (Int, Int) -> Float
) : BaseMatrix<Float>(), Matrix.Mutable<Float> {
    constructor(
        width: Int,
        height: Int,
        mut: Mut = Mut(),
        initialValue: Float = 0f
    ) : this(width, height, mut, { _, _ -> initialValue })

    override val structure: RingLike<Float> = Structures.RealFPField

    private val array: Array<Array<Float>> = Array(width, { i -> Array(height, { j -> initialValueFunction(i, j) }) })
    override fun get(i: Int, j: Int): Float = array[i][j]
    override fun set(i: Int, j: Int, value: Float) {
        array[i][j] = value
    }

//    fun MUL(other: ArrayMatrix) {
//        val result = Array(width, { Array(other.height, { 0 }) })
//        for (i in 0 until result.size)
//            for (j in 0 until a.size)
//                for (k in 0 until b.size)
//                    result[i][j] += a[i][k] + b[k][j]
//        return result
//    }
}