package ir.smmh.nile

/**
 * A 'mut', or a mutation controller, is an object that is either 'dirty' or
 * not. It signifies the mutation of a certain piece of mutable data, and
 * whether or not some, potentially computationally intensive "cleaning up"
 * behavior has taken place after that mutation or not; i.e. it is dirty or not.
 * Though you can customize `onPreMutate` and `onMutate` (which are called before
 * and after every mutation respectively) as well as `onClean`, it is better to
 * focus your heavy code on the latter since cleaning is much less frequent than
 * mutation.
 *
 * Note that a mut will only be tainted if the data actually changes, not if
 * a change was attempted. For example clearing an empty list should not taint
 * its mut.
 */
class Mut(
    onPreMutate: (() -> Unit)? = null,
    onMutate: (() -> Unit)? = null,
    onClean: (() -> Unit)? = null,
) {

    /**
     * A mutable object is an object that has mutation controller, or a "mut", and
     * whenever it gets mutated, it calls the mut's `preMutate` (before the mutation)
     * and `mutate` (after the mutation) methods. Along with customizable behavior
     * for each method, this marks the mut as 'dirty'. If we call the mut's `clean`
     * method and it is 'dirty', some customized behavior is executed, and if no
     * exceptions occur, it is marked as 'not dirty'.
     */
    interface Able {
        val mut: Mut
    }

    val onPreMutate: MutableList<() -> Unit> = ArrayList()
    val onMutate: MutableList<() -> Unit> = ArrayList()
    val onClean: MutableList<() -> Unit> = ArrayList()

    private var dirty = true
        get

    fun preMutate() {
        onPreMutate.forEach { it() }
    }

    fun mutate() {
        taint()
        onMutate.forEach { it() }
    }

    fun taint() {
        dirty = true
    }

    fun clean() {
        if (dirty) {
            onClean.forEach { it() }
            dirty = false
        }
    }

    fun merge(other: Mut) {
        onPreMutate.addAll(other.onPreMutate)
        onMutate.addAll(other.onMutate)
        onClean.addAll(other.onClean)
    }

    init {
        if (onPreMutate != null) this.onPreMutate.add(onPreMutate)
        if (onMutate != null) this.onMutate.add(onMutate)
        if (onClean != null) this.onClean.add(onClean)
    }
}