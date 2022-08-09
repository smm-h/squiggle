@file:Suppress("UNCHECKED_CAST")

package ir.smmh.nile.or

/**
 * Takes half the size in memory, but does one unchecked cast per get.
 *
 * @see FatOr
 */
class SlimOr<This, That>(private val core: Any, override val isThis: Boolean) : AbstractOr<This, That>() {

    override fun getThis(): This {
        return core as This
    }

    override fun getThat(): That {
        return core as That
    }

    companion object {
        fun <This, That> makeThis(core: This): Or<This, That> {
            return either(core, null)
        }

        fun <This, That> makeThat(core: That): Or<This, That> {
            return either(null, core)
        }

        fun <This, That> either(thisObject: This?, thatObject: That?): Or<This, That> {
            require(thisObject == null != (thatObject == null)) { "either both or neither of values are null" }
            val isThis = thatObject == null
            return SlimOr(if (isThis) thisObject!! else thatObject!!, isThis)
        }
    }
}