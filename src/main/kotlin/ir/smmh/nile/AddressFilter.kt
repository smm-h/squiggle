package ir.smmh.nile

interface AddressFilter<T> {

    operator fun contains(address: List<T>): Boolean

    interface Mutable<T> : AddressFilter<T> {

        operator fun plusAssign(address: List<T>)

        fun clear()

        @Suppress("UNCHECKED_CAST")
        class Impl<T> : Mutable<T> {
            private val filter: MutableMap<T, Any> = HashMap()

            override fun plusAssign(address: List<T>): Unit =
                plusAssign(filter, address, 0)

            private fun plusAssign(map: MutableMap<T, Any>, address: List<T>, offset: Int): Unit =
                plusAssign(map.computeIfAbsent(address[offset]) { HashMap<T, Any>() } as MutableMap<T, Any>,
                    address,
                    offset + 1)

            override fun contains(address: List<T>): Boolean =
                contain(filter, address, 0)

            private fun contain(map: Map<T, Any>, address: List<T>, offset: Int): Boolean =
                contain(
                    map.getOrDefault(address[offset], emptyMap<T, Any>()) as Map<T, Any>,
                    address,
                    offset + 1
                )

            override fun clear() {
                filter.clear()
            }
        }
    }
}
