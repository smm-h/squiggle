package ir.smmh.math.tuple

import ir.smmh.math.MathematicalException

class TupleIndexOutOfBoundsException(val index: Int) :
    MathematicalException("index $index out of bounds")