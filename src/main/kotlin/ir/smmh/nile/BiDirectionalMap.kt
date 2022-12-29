package ir.smmh.nile

class BiDirectionalMap<T, R>(original: List<T>, mapped: List<R>) : HasSize {
    //constructor(original: List<T>, map: (T) -> R) : this(original, original.map(map))
    //constructor(original: Iterable<T>, map: (T) -> R) : this(original.toList(), original.map(map))

    override val size = original.size

    private val f = HashMap<T, R>()
    private val b = HashMap<R, T>()

    val forward: Map<T, R> get() = f
    val backward: Map<R, T> get() = b

    init {
        require(size == mapped.size)
        (0 until size).forEach { i ->
            f.put(original[i], mapped[i])
            b.put(mapped[i], original[i])
        }
    }
}