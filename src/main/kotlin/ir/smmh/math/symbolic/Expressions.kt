package ir.smmh.math.symbolic

import ir.smmh.math.symbolic.Operator.Binary.Infix.Companion.Plus

//object Expressions {
//    val set = UniversalSet<Expression> {}
//    val ring = RingLike(
//        set,
//        GroupLike(
//            set,
//            Expression::add,
//            Expression::negate,
//            Expression.ZERO,
//            Property.Holder.empty,
//        ),
//        GroupLike(
//            set,
//            Expression::multiply,
//            Expression::invert,
//            Expression.ONE,
//            Property.Holder.empty,
//        ),
//        Expression::subtract,
//        Expression::divide,
//        Property.Holder.empty,
//        Property.Holder.empty,
//    )
//}

fun main() {
    RealCalculator.express(Plus(1, 2)).show()
}