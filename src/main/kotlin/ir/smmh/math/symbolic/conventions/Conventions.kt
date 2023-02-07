package ir.smmh.math.symbolic.conventions

fun interface Conventions {
    operator fun get(convention: Convention): Int

    interface Hierarchical : Conventions {

        val parent: Conventions?
        fun find(convention: Convention): Int?

        override fun get(convention: Convention): Int =
            find(convention) ?: (parent ?: Defaults)[convention]
    }

    object Defaults : Conventions {
        override fun get(convention: Convention) = 0
    }
}