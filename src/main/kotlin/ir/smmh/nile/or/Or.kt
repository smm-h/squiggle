package ir.smmh.nile.or

/**
 * The base class for "this or that" object, which holds exactly one object of
 * type either This or That.
 */
interface Or<This, That> {
    val isThis: Boolean
    val isThat: Boolean
        get() = !isThis

    fun getThis(): This
    fun getThat(): That
    fun sameTypeAs(other: Or<This, That>) = isThis == other.isThis

    fun equalTo(other: Or<This, That>) =
        if (sameTypeAs(other) && isThis) getThis() == other.getThis() else getThat() == other.getThat()

    fun getObject(): Any? =
        if (isThis) getThis() else getThat()

    companion object {
        fun <T> generalize(or: Or<out T, out T>): T =
            if (or.isThis) or.getThis() else or.getThat()
    }
}