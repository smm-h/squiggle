@file:Suppress("unused")

package ir.smmh.serialization.json
// TODO giant-appendable-array

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.TokenizationUtil.toCharSet
import ir.smmh.markup.Html
import ir.smmh.nile.Cache
import ir.smmh.nile.Mut
import ir.smmh.nile.verbs.*
import ir.smmh.util.FunctionalUtil.map
import ir.smmh.util.ReflectUtil.intendedName
import ir.smmh.util.StringUtil.getStringAndClear
import kotlin.math.pow
import kotlin.reflect.KClass

object Json : Language.HasFileExt.Impl("json"), Language.Serialization, Language.Processable {

    override val process = Code.Process {
        val v = parse(it.string)
        it[parsedValue] = v
        it[Html.syntaxHighlighting] = Html.SyntaxHighlighting.Impl("json").also { v.highlight(it) }
    }

    val parsedValue = Code.Aspect<Json.Value>("value")

    private const val tab = "    "

    const val classifierKey = ".CLASSIFIER"

    private val serializableClasses: MutableSet<KClass<*>> = HashSet()
    private val deserializableClasses: MutableSet<KClass<*>> = HashSet()
    private val serializers: MutableMap<String, Serializer<*>> = HashMap()
    private val deserializers: MutableMap<String, Deserializer<*>> = HashMap()

    fun findSerializer(classifier: String): Serializer<*> =
        serializers[classifier] ?: throw Exception("no serializer registered for classifier: $classifier")

    fun findDeserializer(classifier: String): Deserializer<*> =
        deserializers[classifier] ?: throw Exception("no deserializer registered for classifier: $classifier")

    override fun serialize(it: Any?): String = Value.of(it).serialization
    override fun deserialize(string: String): Any? = parse(string).deserialization

    fun interface Serializer<in T : Any> {
        fun serialize(from: T): Json.Object

        companion object {
            fun <T : Any> register(c: KClass<T>, serializer: Serializer<T>) {
                serializers[c.intendedName] = serializer
                serializableClasses.add(c)
            }
        }

        class Gradual<T : Any>(private val elements: Iterable<Pair<String, (T) -> Any?>>) : Serializer<T> {

            constructor(vararg pairs: Pair<String, (T) -> Any?>) : this(pairs.toList())

            override fun serialize(from: T) = serializeTo(from, Json.Object.Mutable.empty())
            fun serializeTo(from: T, to: Json.Object.Mutable): Json.Object = to.apply {
                for (e in elements) this[e.first] = e.second(from)
            }
        }
    }

    fun interface Deserializer<out T : Any> {
        fun deserialize(from: Json.Object): T

        companion object {
            fun <T : Any> register(c: KClass<T>, deserializer: Deserializer<T>) {
                deserializers[c.intendedName] = deserializer
                deserializableClasses.add(c)
            }
        }
    }

    interface SerializerAndDeserializer<T : Any> : Serializer<T>, Deserializer<T> {

        companion object {
            fun <T : Any> registerBoth(c: KClass<T>, serializer: Serializer<T>, deserializer: Deserializer<T>) {
                val name = c.intendedName
                serializableClasses.add(c)
                deserializableClasses.add(c)
                serializers[name] = serializer
                deserializers[name] = deserializer
            }

            fun <T : Any> registerBoth(c: KClass<T>, both: SerializerAndDeserializer<T>) {
                registerBoth(c, both, both)
            }
        }

        class Gradual<T : Any>(
            private val elements: Iterable<Triple<String, (T) -> Any?, T.(Any?) -> Unit>>,
            private val blank: () -> T,
        ) : SerializerAndDeserializer<T> {

            constructor(vararg elements: Triple<String, (T) -> Any?, T.(Any?) -> Unit>, blank: () -> T) :
                    this(elements.toList(), blank)

            override fun serialize(from: T) = serializeTo(from, Json.Object.Mutable.empty())
            fun serializeTo(from: T, to: Json.Object.Mutable): Json.Object = to.apply {
                for (e in elements) this[e.first] = e.second(from)
            }

            override fun deserialize(from: Json.Object) = deserializeTo(from, blank())
            fun deserializeTo(from: Json.Object, to: T): T = to.apply {
                for (e in elements) e.third(this, from[e.first])
            }
        }
    }

    fun parse(string: String): Value {
        return when (val s = string.trim()) {
            "" -> throw Exception("empty json")
            "null" -> Null
            "true" -> True
            "false" -> False
            else -> {
                val n = s.length - 1
                when (s[0]) {
                    '"' -> {
                        if (s == "\"") {
                            throw Exception("lone '\"'")
                        } else if (s[n] == '"') {
                            WrappedString.quoted(s.substring(1, n))
                        } else {
                            throw Exception("missing closer '\"'")
                        }
                    }
                    '[' -> {
                        if (s[n] == ']') {
                            LazyArray(s.substring(1, n).trim())
                        } else {
                            throw Exception("missing closer ']'")
                        }
                    }
                    '{' -> {
                        if (s[n] == '}') {
                            LazyObject(s.substring(1, n).trim())
                        } else {
                            throw Exception("missing closer '}'")
                        }
                    }
                    else -> {
                        if ((s.toCharSet() - numberCharSet).isEmpty())
                            parseNumber(s)
                        else
                            throw Exception("parsing failed $s")
                    }
                }
            }
        }
    }

    private fun parseArray(string: String): List<Value> {
        if (string.isEmpty() || string.isBlank()) return emptyList()
        val commaTerminatedString = "$string,"
        val data: MutableList<Value> = ArrayList()
        var previous: Char = 0.toChar()
        var inString = false
        var objects = 0
        var arrays = 0
        val builder = StringBuilder()
        var append = true
        for (index in commaTerminatedString.indices) {
            val character = commaTerminatedString[index]
            if (inString) {
                inString = stillInString(character, previous != '\\')
            } else {
                when (character) {
                    ',' -> if (objects == 0 && arrays == 0) {
                        data.add(parse(builder.getStringAndClear()))
                        append = false
                    }
                    '"' -> inString = true
                    '{' -> objects++
                    '[' -> arrays++
                    '}' -> if (objects > 0) objects-- else throw Exception("unbalanced closer '$character'")
                    ']' -> if (arrays > 0) arrays-- else throw Exception("unbalanced closer '$character'")
                    else -> if (objects == 0 && arrays == 0 && character.isWhitespace()) append = false
                }
            }
            previous = character
            if (append) builder.append(character) else append = true
        }
        if (inString) throw Exception("incomplete string")
        if (objects != 0 || arrays != 0) throw Exception("unbalanced")
        return data
    }

    private fun parseObject(string: String): Map<String, Value> {
        if (string.isEmpty() || string.isBlank()) return emptyMap()
        val commaTerminatedString = "$string,"
        val data: MutableMap<String, Value> = HashMap()
        var previous: Char = 0.toChar()
        var inString = false
        var objects = 0
        var arrays = 0
        val builder = StringBuilder()
        var key: String? = null
        var append = true
        for (index in commaTerminatedString.indices) {
            val character = commaTerminatedString[index]
            if (inString) {
                inString = stillInString(character, previous != '\\')
            } else {
                when (character) {
                    ':' -> if (objects == 0 && arrays == 0) {
                        if (key != null) throw Exception("missing value") else {
                            key = builder.getStringAndClear()
                            append = false
                        }
                    }
                    ',' -> if (objects == 0 && arrays == 0) {
                        if (key == null) throw Exception("missing key") else {
                            val n = key.length - 1
                            if (n > 0 && key[0] == '"' && key[n] == '"') {
                                data[key.substring(1, n)] = parse(builder.getStringAndClear())
                                key = null
                                append = false
                            } else {
                                throw Exception("invalid key: '$key'")
                            }
                        }
                    }
                    '"' -> inString = true
                    '{' -> objects++
                    '[' -> arrays++
                    '}' -> if (objects > 0) objects-- else throw Exception("unbalanced closer '$character'")
                    ']' -> if (arrays > 0) arrays-- else throw Exception("unbalanced closer '$character'")
                    else -> if (objects == 0 && arrays == 0 && character.isWhitespace()) append = false
                }
            }
            previous = character
            if (append) builder.append(character) else append = true
        }
        if (inString) throw Exception("incomplete string")
        if (objects != 0 || arrays != 0) throw Exception("unbalanced")
        return data
    }

    private fun stillInString(character: Char, unescaped: Boolean): Boolean {
        when (character) {
            '"' -> if (unescaped) return false
//            '/' -> if (unescaped) throw Exception("unescaped '/'")
            '\b' -> throw Exception("unescaped '\\b'")
            '\u000C' -> throw Exception("unescaped '\\f'")
            '\n' -> throw Exception("unescaped '\\n'")
            '\r' -> throw Exception("unescaped '\\r'")
            '\t' -> throw Exception("unescaped '\\t'")
        }
        return true
    }

    private class LazyArray(val inside: String) : Array {
        private val data: List<Value> by lazy { parseArray(inside) }
        override val deserialization: Any by lazy { data.map { it.deserialization } }

        override fun subValue(index: Int): Value {
            validateIndex(index)
            return data[index]
        }

        override fun overSubValues(): Iterable<Value> = data
        override val serialization = "[$inside]"
        override fun iterator() = data.iterator().map { it.deserialization }
        override fun getAtIndex(index: Int) = subValue(index).deserialization
        override val size get() = data.size
        override fun clone(deepIfPossible: Boolean) = LazyArray(inside)
        override fun toString() = "Json.Array:$serialization"
        override fun containsValue(toCheck: Any?): Boolean = data.contains(Value.of(toCheck))
    }

    private class LazyObject(val inside: String) : Object {
        private val data: Map<String, Value> by lazy { parseObject(inside) }
        override val deserialization: Any? by lazy {
            val classifier = data[classifierKey]
            if (classifier == null) data.keys.associateWith { data[it]!!.deserialization }
            else findDeserializer(classifier.deserialization as String).deserialize(this)
        }

        override fun subValue(key: String) = data[key]
        override val serialization = "{$inside}"
        override fun iterator(): Iterator<String> = data.keys.iterator()
        override fun getAtPlace(place: String) = subValue(place)?.deserialization
        override fun containsPlace(toCheck: String) = toCheck in data
        override val size get() = data.size
        override fun clone(deepIfPossible: Boolean) = LazyObject(inside)
        override fun toString() = "Json.Object:$serialization"
    }

    // -?([1-9][0-9]*|0)(\.[0-9]+)?((e|E)(\+|-|)[0-9]+)?

    private val numberCharSet = "0123456789+-.eE".toCharSet()

    private fun parseNumber(string: String): Value {
        val number = string.lowercase()
        try {
            return WrappedNumber(
                when (number.count { it == 'e' }) {
                    0 -> if ('.' in number) number.toDouble() else number.toInt()
                    1 -> {
                        val (base, exponent) = number.split('e')
                        base.toDouble().pow(exponent.toDouble())
                    }
                    else -> throw Exception("more than one E present")
                }
            )
        } catch (e: NumberFormatException) {
            throw Exception("invalid number")
        }
    }

    interface Enum {
        val identifier: String

        companion object {

            fun of(data: String): kotlin.Enum<*>? {
                return try {
                    cache(data)
                } catch (e: Cache.Exception) {
                    null
                }
            }

            private val cache: (String) -> kotlin.Enum<*> = Cache {
                val (name, id) = it.split("@")
                (classes[id] ?: throw Exception("Enum class was not identified: $id")).java.enumConstants.first { e ->
                    (e as kotlin.Enum<*>).name == name
                } as kotlin.Enum<*>
            }

            private val classes: MutableMap<String, KClass<*>> = HashMap()

            /**
             * Add an enum class to the list of serializable enum classes
             *
             * @return whether it was already added
             */
            fun <E> register(it: E): Boolean where E : kotlin.Enum<*>, E : Enum {
                val itsIdentifier = it.identifier
                val itsClass = it::class
                return when (classes[itsIdentifier]) {
                    null -> {
                        classes[itsIdentifier] = itsClass
                        true
                    }
                    itsClass -> false
                    else -> throw Exception("two different Enums have the same identity: $itsIdentifier")
                }
            }
        }
    }

    class Exception(message: String) : kotlin.Exception(message)

    sealed interface Value {
        val serialization: String
        val deserialization: Any?
        fun query(address: String) = query(ArrayDeque(address.split('/')))
        fun query(q: ArrayDeque<String>): Value

        fun toCode() = Code(serialization, Json)

        fun highlight(it: Html.SyntaxHighlighting.Impl, depth: Int = 0)

        companion object {
            fun <T> of(it: T) = valueOfNullable(it, null)
        }
    }

    private fun <T> valueOfNullable(it: T, parent: MutableStructure?): Value {
        return if (it == null) Null else valueOf(it, parent)
    }

    private fun <T : Any> valueOf(it: T, parent: MutableStructure?): Value {
        return when (it) {
            is Value -> it
            is Boolean -> if (it) True else False
            is Number -> WrappedNumber(it)
            is String -> WrappedString.unquoted(it)
            is kotlin.Enum<*> -> {
                if (it is Enum) {
                    Enum.register(it)
                    WrappedString.unquoted(it.name + "@" + it.identifier)
                } else throw Exception("Enum must extend Json.Enum: ${it::class}")
            }
            is kotlin.Array<*> -> ArrayImpl.of(it.asIterable(), parent)
            is Map<*, *> -> ObjectImpl.of(it, parent)
            else -> {
                val c = serializableClasses.filter { c -> c.isInstance(it) }
                when (c.size) {
                    0 -> {
                        if (it is Iterable<*>) ArrayImpl.of(it, parent)
                        else throw Exception("could not convert object to Json.Value: $it")
                    }
                    1 -> {
                        @Suppress("UNCHECKED_CAST")
                        try {
                            val classifier = c.single().intendedName
                            val serializer = findSerializer(classifier) as Serializer<T>
                            val serialized = serializer.serialize(it)
                            (serialized as Object.Mutable).also {
                                it[classifierKey] = classifier
                            }
                        } catch (e: kotlin.Exception) {
                            System.err.println("SERIALIZER FAILED")
                            throw e
                        }
                    }
                    else -> /* TODO */ throw Exception("ambiguity")
                }
            }
        }
    }

    private sealed class ImmutableValue(override val serialization: String, override val deserialization: Any?) :
        Value {
        override fun query(q: ArrayDeque<String>) = if (q.isEmpty()) this else throw Exception("cannot query $q")
    }

    private object Null : ImmutableValue("null", null) {
        override fun toString() = "Json.Null"
        override fun highlight(it: Html.SyntaxHighlighting.Impl, depth: Int) {
            it.add(serialization, "constant null")
        }
    }

    private object True : ImmutableValue("true", true) {
        override fun toString() = "Json.True"
        override fun highlight(it: Html.SyntaxHighlighting.Impl, depth: Int) {
            it.add(serialization, "constant true")
        }
    }

    private object False : ImmutableValue("false", false) {
        override fun toString() = "Json.False"
        override fun highlight(it: Html.SyntaxHighlighting.Impl, depth: Int) {
            it.add(serialization, "constant false")
        }
    }

    private class WrappedString private constructor(quoted: String, unquoted: String) :
        ImmutableValue(quoted, Enum.of(unquoted) ?: unquoted) {
        override fun toString() = "Json.String:$deserialization"

        companion object {
            fun quoted(quoted: String) = WrappedString(quoted, unquote(quoted))
            fun unquoted(unquoted: String) = WrappedString(quote(unquoted), unquoted)
        }

        override fun highlight(it: Html.SyntaxHighlighting.Impl, depth: Int) {
            if (deserialization is Enum) {
                it.add("\"", "literal string")
                it.add((deserialization as kotlin.Enum<*>).name, "literal string enum_name")
                it.add("@" + deserialization.identifier, "literal string enum_id")
                it.add("\"", "literal string")
            } else {
                it.add("\"$serialization\"", "literal data string")
            }
        }
    }

    fun unquote(quoted: String): String {
        var index = 0
        val length = quoted.length
        val builder = StringBuilder(length)
        while (index < length) {
            val char = quoted[index]
            val decoded: Char = if (char == '\\') {
                index++
                val nextChar = quoted[index]
                if (nextChar == 'U') throw Exception("uppercase U")
                when (nextChar.lowercaseChar()) {
                    '"' -> '\"'
                    '\\' -> '\\'
                    '/' -> '/'
                    'b' -> '\b'
                    'f' -> '\u000C'
                    'n' -> '\n'
                    'r' -> '\r'
                    't' -> '\t'
                    'u' -> {
                        index += 4
                        try {
                            quoted.substring(index - 3, index + 1).toInt(16).toChar()
                        } catch (e: StringIndexOutOfBoundsException) {
                            throw Exception("incomplete unicode escape")
                        } catch (e: NumberFormatException) {
                            throw Exception("invalid unicode escape")
                        }
                    }
                    else -> throw Exception("illegal escape: $nextChar")
                }
            } else char
            builder.append(decoded)
            index++
        }
        return builder.toString()
    }

    fun quote(unquoted: String) = unquoted.asIterable().joinToString("", "\"", "\"") {
        when (it) {
            '"' -> "\\\""
            '\\' -> "\\\\"
            '/' -> "\\/"
            '\b' -> "\\b"
            '\u000C' -> "\\f"
            '\n' -> "\\n"
            '\r' -> "\\r"
            '\t' -> "\\t"
            else -> it.toString()
        }
    }

    private class WrappedNumber(val data: Number) : ImmutableValue(data.toString(), data) {
        override fun toString() = "Json.Number:$data"
        override fun highlight(it: Html.SyntaxHighlighting.Impl, depth: Int) {
            it.add(serialization, "literal number")
        }
    }

    private sealed class MutableStructure() :
        Structure.Mutable, Mut.Able {
        protected abstract val parent: MutableStructure?
        protected abstract fun serialize(): String
        protected abstract fun deserialize(): Any?
        override final val mut: Mut = Mut(onMutate = this::refOnMutate)

        private fun refOnMutate() {
            parent?.mut?.preMutate()
            serializationMut.taint()
            deserializationMut.taint()
            parent?.mut?.mutate()
        }

        private val serializationMut = Mut(onClean = { this@MutableStructure.cleanSerialization() })
        private val deserializationMut = Mut(onClean = { this@MutableStructure.cleanDeserialization() })

        override final var serialization: String = ""
            get() {
                serializationMut.clean(); return field
            }
            private set

        override var deserialization: Any? = null
            get() {
                deserializationMut.clean(); return field
            }

        private fun cleanSerialization() {
            serialization = serialize()
        }

        private fun cleanDeserialization() {
            deserialization = deserialize()
        }

        abstract fun clone(deepIfPossible: Boolean, clonedParent: MutableStructure?): MutableStructure
    }

    sealed interface Structure : Value, CanClone<Structure> {
        override fun specificThis() = this

        sealed interface Mutable : Structure, CanClear, CanClone.Mutable<Structure> {
            override fun clone(deepIfPossible: Boolean): Structure.Mutable
            override fun clone(deepIfPossible: Boolean, mut: Mut): Structure.Mutable
        }
    }

    sealed interface Array : Structure, Iterable<Any?>, CanGetAtIndex<Any?>, CanContainValue<Any?> {

        operator fun get(index: Int): Any? = getNullableAtIndex(index)

        override fun clone(deepIfPossible: Boolean): Array

        sealed interface Mutable : Array, Structure.Mutable, CanInsertAtIndex<Any?>,
            CanRemoveAt, CanSwapAtIndices<Any?>, CanRemoveElementFrom<Any?> {
            operator fun set(index: Int, value: Any?) = setAtIndex(index, value)

            override fun clone(deepIfPossible: Boolean): Array.Mutable
            override fun clone(deepIfPossible: Boolean, mut: Mut): Array.Mutable

            companion object {
                fun of(vararg elements: Any?) = Value.of(listOf(*elements)) as Mutable
                fun empty(): Mutable = ArrayImpl(emptyList(), null)
            }
        }

        companion object {
            fun of(vararg elements: Any?) = Mutable.of(*elements) as Array
            fun empty(): Array = empty
            private val empty = LazyArray("")
        }

        fun subValue(index: Int): Value

        fun overSubValues(): Iterable<Value>

        override fun query(q: ArrayDeque<String>) =
            if (q.isEmpty()) this else subValue(q.removeFirst().toInt()).query(q)

        override fun highlight(it: Html.SyntaxHighlighting.Impl, depth: Int) {
            if (size == 0) {
                it.add("[]", "structure array_brackets")
            } else {
                it.add("[", "structure array_brackets")
                for (index in 0 until size) {
                    it.add("\n", "whitespace")
                    it.add(tab.repeat(depth + 1), "whitespace")
                    subValue(index).highlight(it, depth + 1)
                    it.add(",", "structure array_comma comma")
                }
                it.removeLast()
                it.add("\n", "whitespace")
                it.add(tab.repeat(depth), "whitespace")
                it.add("]", "structure array_brackets")
            }
        }
    }

    sealed interface Object : Structure, Iterable<String>, CanGetAtPlace<String, Any?> {

        operator fun get(key: String): Any? = getNullableAtPlace(key)
        operator fun contains(key: String): Boolean = containsPlace(key)

        override fun clone(deepIfPossible: Boolean): Object

        sealed interface Mutable : Object, Structure.Mutable, CanRemoveAtPlace<String>,
            CanSwapAtPlaces<String, Any?>, CanRemoveElementFrom<Any?> {

            operator fun set(key: String, value: Any?) = setAtPlace(key, value)

            override fun clone(deepIfPossible: Boolean): Object.Mutable
            override fun clone(deepIfPossible: Boolean, mut: Mut): Object.Mutable

            companion object {
                fun of(vararg pairs: Pair<String, Any?>) = Value.of(mapOf(*pairs)) as Mutable
                fun empty(): Mutable = ObjectImpl(emptyMap(), null)
            }
        }

        companion object {
            fun of(vararg pairs: Pair<String, Any?>) = Mutable.of(*pairs) as Object
            fun empty(): Object = empty
            private val empty = LazyObject("")
        }

        fun subValue(key: String): Value?

        override fun query(q: ArrayDeque<String>): Value {
            return if (q.isEmpty()) this else {
                val k = q.removeFirst()
                subValue(k)?.query(q) ?: throw Exception("could not query $k in $this")
            }
        }

        override fun highlight(it: Html.SyntaxHighlighting.Impl, depth: Int) {
            if (size == 0) {
                it.add("{}", "structure object_braces")
            } else {
                it.add("{", "structure object_braces")
                for (key in this) {
                    it.add("\n", "whitespace")
                    it.add(tab.repeat(depth + 1), "whitespace")
                    it.add("\"$key\"", "object_key string")
                    it.add(":", "structure object_colon")
                    it.add(" ", "whitespace")
                    subValue(key)!!.highlight(it, depth + 1)
                    it.add(",", "structure object_comma comma")
                }
                it.removeLast()
                it.add("\n", "whitespace")
                it.add(tab.repeat(depth), "whitespace")
                it.add("}", "structure object_braces")
            }
        }
    }

    private class ArrayImpl(
        data: List<Value>,
        override val parent: MutableStructure?,
    ) : MutableStructure(),
        Array.Mutable {

        private val data: MutableList<Value> = data.toMutableList()

        override fun serialize() = data.joinToString(",", "[", "]") { it.serialization }
        override fun deserialize() = data.map { it.deserialization }
        override fun iterator(): Iterator<Any?> = data.iterator().map { it.deserialization }

        override fun subValue(index: Int): Value {
            validateIndex(index)
            return data[index]
        }

        override fun overSubValues(): Iterable<Value> = data

        override fun clear() = data.clear()

        override val size get() = data.size

        override fun clone(deepIfPossible: Boolean) = clone(deepIfPossible, Mut())
        override fun clone(deepIfPossible: Boolean, mut: Mut) =
            clone(deepIfPossible, null).also { it.mut.merge(mut) } as Array.Mutable

        override fun clone(
            deepIfPossible: Boolean,
            clonedParent: MutableStructure?,
        ): MutableStructure {
            val v = ArrayImpl(emptyList(), clonedParent)
            for (i in data) v.data.add(if (i is MutableStructure) i.clone(deepIfPossible, v) else i)
            return v
        }

        override fun insertAtIndex(index: Int, toInsert: Any?) {
            validateBetweenIndices(index)
            mut.preMutate()
            data.add(index, Value.of(toInsert))
            mut.mutate()
        }

        override fun removeElementFrom(toRemove: Any?) {
            mut.preMutate()
            data.remove(Value.of(toRemove))
            mut.mutate()
        }

        override fun containsValue(toCheck: Any?) = data.contains(Value.of(toCheck))

        override fun removeIndexFrom(toRemove: Int) {
            validateIndex(toRemove)
            mut.preMutate()
            data.removeAt(toRemove)
            mut.mutate()
        }

        override fun getAtIndex(index: Int) = subValue(index).deserialization

        override fun setAtIndex(index: Int, toSet: Any?) {
            validateIndex(index)
            mut.preMutate()
            data[index] = Value.of(toSet)
            mut.mutate()
        }

        override fun toString() = "Json.Array:$data"

        companion object {
            fun of(it: Iterable<*>, parent: MutableStructure?, mut: Mut = Mut()): Array.Mutable {
                val v = ArrayImpl(emptyList(), parent).also { it.mut.merge(mut) }
                for (i in it) v.data.add(valueOfNullable(i, v))
                return v
            }
        }
    }

    private class ObjectImpl(
        data: Map<String, Value>,
        override val parent: MutableStructure?,
    ) : MutableStructure(),
        Object.Mutable {
        private val data: MutableMap<String, Value> = data.toMutableMap()

        override fun serialize() = data.keys.joinToString(",", "{", "}") { "\"$it\":" + data[it]!!.serialization }
        override fun deserialize() = {
            val classifier = data[classifierKey]
            if (classifier == null) data.keys.associateWith { data[it]!!.deserialization }
            else findDeserializer(classifier.deserialization as String).deserialize(this)
        }()

        override fun iterator() = data.keys.iterator()

        override fun subValue(key: String): Value? = data[key]

        override fun clear() = data.clear()

        override val size get() = data.size

        override fun clone(deepIfPossible: Boolean) = clone(deepIfPossible, Mut())
        override fun clone(deepIfPossible: Boolean, mut: Mut) =
            clone(deepIfPossible, null).also { it.mut.merge(mut) } as Object.Mutable

        override fun clone(
            deepIfPossible: Boolean,
            clonedParent: MutableStructure?,
        ): MutableStructure {
            val v = ObjectImpl(emptyMap(), clonedParent)
            for ((k, i) in data) v.data[k] = if (i is MutableStructure) i.clone(deepIfPossible, v) else i
            return v
        }

        override fun removeElementFrom(toRemove: Any?) {
            mut.preMutate()
            val elementToRemove = Value.of(toRemove)
            while (true) {
                val pair = data.entries.find { it.value == elementToRemove }
                if (pair == null) break else data.remove(pair.key)
            }
            mut.mutate()
        }

        override fun containsValue(toCheck: Any?) = data.containsValue(Value.of(toCheck))
        override fun containsPlace(toCheck: String) = data.containsKey(toCheck)

        override fun getAtPlace(place: String) = subValue(place)?.deserialization

        override fun removeAtPlace(toRemove: String) {
            mut.preMutate()
            data.remove(toRemove)
            mut.mutate()
        }

        override fun setAtPlace(place: String, toSet: Any?) {
            mut.preMutate()
            data[place] = Value.of(toSet)
            mut.mutate()
        }

        override fun toString() = "Json.Object:$data"

        companion object {
            fun of(it: Map<*, *>, parent: MutableStructure?, mut: Mut = Mut()): Object.Mutable {
                val v = ObjectImpl(emptyMap(), parent).also { it.mut.merge(mut) }
                for ((k, i) in it) v.data[k as String] = valueOfNullable(i, v)
                return v
            }
        }
    }
}