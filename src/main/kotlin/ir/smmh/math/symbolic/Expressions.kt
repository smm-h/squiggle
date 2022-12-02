package ir.smmh.math.symbolic

import ir.smmh.math.abstractalgebra.GroupLike
import ir.smmh.math.abstractalgebra.Property
import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.UniversalNumberSets.IntIntegers

object Expressions {
    val ZERO = Expression.of(0)
    val ONE = Expression.of(1)
    val set = Set.Specific.Uncountable.Universal<Expression> { Expression.of(IntIntegers.choose()) }
    val ring = RingLike(
        set,
        GroupLike(
            set,
            Operator.Binary.Plus::invoke,
            Operator.Unary.Minus::invoke,
            ZERO,
            Property.Holder.empty,
        ),
        GroupLike(
            set,
            Operator.Binary.Cross::invoke,
            { Operator.Binary.Over(ONE, it) },
            ONE,
            Property.Holder.empty,
        ),
        Operator.Binary.Minus::invoke,
        Operator.Binary.Over::invoke,
        Property.Holder.empty,
        Property.Holder.empty,
    )
}