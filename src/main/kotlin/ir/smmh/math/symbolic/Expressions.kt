package ir.smmh.math.symbolic

import ir.smmh.math.abstractalgebra.GroupLike
import ir.smmh.math.abstractalgebra.Property
import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.math.settheory.Sets.Integer32
import ir.smmh.math.settheory.UniversalSet

object Expressions {
    val ZERO = Expression.of(0)
    val ONE = Expression.of(1)
    val set = UniversalSet<Expression> { Expression.of(Integer32.pick()) }
    val ring = RingLike(
        set,
        GroupLike(
            set,
            Operator.Binary.Infix.Plus::invoke,
            Operator.Unary.Prefix.Minus::invoke,
            ZERO,
            Property.Holder.empty,
        ),
        GroupLike(
            set,
            Operator.Binary.Infix.Cross::invoke,
            { Operator.Binary.Infix.Over(ONE, it) },
            ONE,
            Property.Holder.empty,
        ),
        Operator.Binary.Infix.Minus::invoke,
        Operator.Binary.Infix.Over::invoke,
        Property.Holder.empty,
        Property.Holder.empty,
    )
}