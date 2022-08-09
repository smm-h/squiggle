package ir.smmh.nile

import ir.smmh.nile.Sequential.AbstractSequential

class SequenceJoiner<T> : AbstractSequential<T>(), Sequential<T> {
    private val sequences: Sequential.Mutable.VariableSize<Sequential<out T>> = SequentialImpl()

    fun join(sequential: Sequential<out T>) {
        sequences.append(sequential)
    }

    fun startOver() {
        sequences.clear()
    }

    override fun getAtIndex(index: Int): T {
        var i = index
        for (s in sequences) {
            val n = s.size
            if (i < n) {
                return s.getAtIndex(i)
            } else {
                i -= n
            }
        }
        throw IndexOutOfBoundsException()
    }

    companion object {
        operator fun <S, T : S, R : S> Sequential<T>.plus(that: Sequential<R>): SequenceJoiner<S> {
            val s = SequenceJoiner<S>()
            if (this is SequenceJoiner) s.sequences.addAll(this.sequences) else s.join(this)
            if (that is SequenceJoiner) s.sequences.addAll(that.sequences) else s.join(that)
            return s
        }
    }

    override val size: Int
        get() {
            var n = 0
            for (s in sequences) {
                n += s.size
            }
            return n
        }
}