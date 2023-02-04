package ir.smmh.math.tuple

import ir.smmh.math.MathematicalObject as M

abstract class AbstractFinitaryTuple : M.Abstract(), Tuple.Finitary {
    override val debugText
            by lazy { (0 until length).joinToString(", ", "(", ")") { get(it).debugText } }
    override val tex
            by lazy { (0 until length).joinToString(",", "{(", ")}") { get(it).tex } }
}