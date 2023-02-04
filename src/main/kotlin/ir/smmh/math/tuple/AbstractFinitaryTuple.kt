package ir.smmh.math.tuple

import ir.smmh.math.MathematicalObject as M

abstract class AbstractFinitaryTuple : M.Abstract(), Tuple.Finitary {
    override val debugText: String by lazy { (0 until length).joinToString(", ", "(", ")") { get(it).debugText } }
    override val tex: String by lazy { (0 until length).joinToString(",", "{(", ")}") { get(it).tex } }
}