package ir.smmh.nile.or

abstract class AbstractOr<This, That> : Or<This, That> {
    override fun equals(other: Any?) =
        (this === other) || getObject() == (if (other is Or<*, *>) other.getObject() else other)

    override fun hashCode() = getObject().hashCode()
    override fun toString() = getObject().toString()
}