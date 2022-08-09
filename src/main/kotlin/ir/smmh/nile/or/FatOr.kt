package ir.smmh.nile.or

/**
 * Takes double the size in memory, but is checked
 *
 * @see SlimOr
 */
class FatOr<This, That> protected constructor(core: Any?, override val isThis: Boolean) :
    AbstractOr<This, That>() {
    private var thisObject: This? = null
    private var thatObject: That? = null

    override fun getThis(): This {
        return thisObject!!
    }

    override fun getThat(): That {
        return thatObject!!
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
            return FatOr(if (isThis) thisObject else thatObject, isThis)
        }
    }

    init {
        @Suppress("UNCHECKED_CAST")
        if (isThis) {
            thisObject = core as This
            thatObject = null
        } else {
            thatObject = core as That
            thisObject = null
        }
    }
}