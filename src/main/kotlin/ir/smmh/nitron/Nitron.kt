@file:Suppress("UNCHECKED_CAST")

package ir.smmh.nitron

import ir.smmh.lingu.Language
import ir.smmh.lingu.Language.Companion.lateFileExt
import ir.smmh.markup.Html.defaultMetadata
import ir.smmh.markup.Markup
import ir.smmh.markup.Markup.joinToFragment
import ir.smmh.nile.table.Table
import ir.smmh.serialization.json.Json
import ir.smmh.util.FileUtil.touch
import ir.smmh.util.FileUtil.writeTo
import java.io.File
import java.util.*

object Nitron : Iterable<String> {

    override fun iterator(): Iterator<String> = minds.keys.iterator()

    private val mindStack: Stack<Mind> = Stack()
    private val currentMind: Mind get() = mindStack.peek()

    private val ideaStack: Stack<Idea> = Stack()
    private val currentIdea: Idea get() = ideaStack.peek()

    operator fun contains(mindName: String) = mindName in minds
    operator fun get(mindName: String) = minds[mindName]

    private val minds: MutableMap<String, Mind> = HashMap()

    /**
     * Types includes ideas whose values are mutable, and built-in
     * primitives like number and string whose values are immutable.
     */
    sealed interface Type {
        val fragment: Markup.Fragment
        val typeName: String

        /**
         * Immutable types
         */
        enum class Primitive : Type, Json.Enum {
            NUMBER, STRING, BOOLEAN, NULL;

            override val identifier = "Nitron.Type.Primitive"
            override val typeName = name
            override val fragment = Markup.Tools.italic(name)
        }
    }

    interface Value {

        /**
         * To avoid serializing Values as Json.Objects, we turn them into
         * Json-friendly primitives such as booleans, numbers, and strings, and
         * then serialize them.
         */
        val data: Any?

        companion object {

            /**
             * This reverses the Json-friendly primitive serialization; i.e.
             * primitives become Values.
             */
            fun of(it: Any?): Value = when (it) {
                null -> FakeNull
                true -> FakeTrue
                false -> FakeFalse
                is Number -> FakeNumber(it)
                is String -> {
                    if (it.isNotEmpty() && it[0] == '#' && '@' in it) {
                        val (index, idea) = it.split('@')
                        Instance(index.substring(1).toInt(), currentMind.imagine(idea))
                    } else {
                        FakeString(it)
                    }
                }
                else -> throw Exception("unknown type")
            }
        }

        fun type(): Type

        private object FakeNull : Value {
            override val data: Any? = null
            override fun type() = Type.Primitive.NULL
            override fun toString() = "Null"
        }

        private object FakeTrue : Value {
            override val data: Boolean = true
            override fun type() = Type.Primitive.BOOLEAN
            override fun toString() = "True"
        }

        private object FakeFalse : Value {
            override val data: Boolean = false
            override fun type() = Type.Primitive.BOOLEAN
            override fun toString() = "False"
        }

        private data class FakeNumber(override val data: Number) : Value {
            override fun type() = Type.Primitive.NUMBER
            override fun toString() = data.toString()
        }

        private data class FakeString(override val data: String) : Value {
            override fun type() = Type.Primitive.STRING
            override fun toString() = data
        }
    }

    sealed class Exception(message: String) : kotlin.Exception(message)

    data class Instance(val index: Int, val idea: Idea) : Value, Iterable<String> {
        override val data = "$index@$idea"

        override fun iterator(): Iterator<String> =
            idea.getInstanceResolutions(index).iterator()

        companion object {
            init {
                Json.Serializer.register(
                    Instance::class, Json.Serializer.Gradual(
                        "index" to { it.index },
                        "idea" to { it.idea.name },
                    )
                )
                Json.Deserializer.register(Instance::class) {
                    Instance(
                        it["index"] as Int,
                        currentMind.imagine(it["idea"] as String),
                    )
                }
            }
        }

        override fun type() = idea

        override fun toString() = "@$idea #$index"

        val fragment = Markup.Tools.link(toString(), idea.url, index.toString())

        fun set(name: String, value: Value?) {
            val resolution = idea.getResolution(name)
            when {
                resolution == null -> throw RuntimeException("could not resolve '$name'")
                value == null || resolution.property.type == value.type() -> resolution[index] = value
                else -> throw RuntimeException(
                    "type mismatch; expected ${resolution.property.type.typeName}, got ${
                        value.type().typeName
                    }"
                )
            }
        }

        operator fun get(name: String): Value? {
            val resolution = idea.getResolution(name)
            if (resolution == null) {
                throw RuntimeException("could not resolve '$name'")
            } else {
                return resolution[index]
            }
        }

        fun disengage() = idea.destroy(index)

        fun initialize(seed: Map<String, Value>) {
            for (k in seed.keys) set(k, seed[k])
        }

        fun initialize(seed: Set<Pair<String, Value>>) {
            for (k in seed) set(k.first, k.second)
        }

        fun toTable(): Markup.Table = idea.getInstanceTable(index).run {
            sortedByColumn(findColumnByName("address")) { it.hashCode() }.toMarkupTable {}
        }

        infix fun isq(general: Idea): Boolean {
            return idea isq general
        }

        fun assertIsq(idea: Idea) {
            assert(this isq idea) { "instance: " + index + " : " + this.idea }
        }

        fun <L> generateFile(language: L): File
                where L : Language.Markup, L : Language.HasFileExt {
            return language.compile(generateDocument(), defaultMetadata) writeTo
                    touch("nitron/${idea.mind.name}/$idea-$index.${language.fileExt}")
        }

        fun generateDocument() = Markup.Document(idea.name + "-" + index) {
            heading("Instance: " + this@Instance.toString()) {
                addSection(toTable())
            }
        }

        fun report(): String = toTable().toString()
    }

    data class Idea(
        val mind: Mind,
        val name: String,
        private var nextKey: Int = 0,
        private val keys: MutableList<Int> = ArrayList(),
        // TODO make fields private
        private val intensions: MutableMap<String, Intension> = HashMap(),
//        private val extensions: MutableMap<String, Extension or Idea> = HashMap(), TODO extensions
        val properties: MutableMap<String, Property> = HashMap(),
        private val reifiedValues: MutableMap<Int, Value> = HashMap(),
        private val reifications: MutableMap<Int, Instance> = HashMap(),
        private val resolutions: MutableMap<String, Resolution> = HashMap(),
    ) : Type, Iterable<Int> {

        override fun iterator(): Iterator<Int> = keys.iterator()

        val url: String get() = "$name.$lateFileExt"

        override fun hashCode() = name.hashCode()

        override fun equals(other: Any?) = other is Idea && other.name == name && other.mind == mind

        companion object {
            init {
                Json.Serializer.register(
                    Idea::class, Json.Serializer.Gradual(
                        "name" to { it.name },
                        "keys" to { it.keys },
                        "nextKey" to { it.nextKey },
                        "intensions" to { it.intensions },
                        "properties" to { it.properties },
                        "reifiedValues" to
                                { it.reifiedValues.mapKeys { it.key.toString() }.mapValues { it.value.data } },
                        "reifications" to { it.reifications.mapKeys { it.key.toString() } },
                    )
                )
                Json.Deserializer.register(Idea::class) {
                    val properties: MutableMap<String, Property> = HashMap()
                    val intensions: MutableMap<String, Intension> = HashMap()
                    ideaStack.push(
                        Idea(
                            currentMind,
                            it["name"] as String,
                            it["nextKey"] as Int,
                            (it["keys"] as List<Int>).toMutableList(),
                            intensions,
                            properties,
                            (it["reifiedValues"] as Map<String, Any?>)
                                .mapKeys { it.key.toInt() }.mapValues { Value.of(it.value) }.toMutableMap(),
                            (it["reifications"] as Map<String, Instance>).mapKeys { it.key.toInt() }.toMutableMap(),
                        )
                    )
                    intensions.putAll(it["intensions"] as Map<String, Intension>)
                    properties.putAll(it["properties"] as Map<String, Property>)
                    ideaStack.pop()
                }
            }
        }

        infix fun isq(idea: Idea): Boolean {
            return idea.name in intensions
        }

//        fun moveProperty(propertyName: String, destination: Idea, transition: Any) {
        // TODO changeOwnership

        private fun resolve(name: String, property: Property, intension: Intension? = null) {
            if (name !in resolutions)
                resolutions[name] = Resolution(intension, property)
        }

        infix fun `is`(idea: Idea): Intension {
            if (this isq idea) {
                println(name + " already is: " + idea.name)
                return intensions[idea.name]!!
            } else {
                if (nextKey == 0) {
                    for (i in idea.intensions.values) {
                        `is`(i.idea)
                    }
                    val intension = Intension(this, idea)
                    intensions[idea.name] = intension
                    for (prop in idea.properties.values) {
                        resolve(prop.name, prop, intension)
                    }
                    return intension
                } else {
                    throw IllegalStateException("idea with instances cannot 'become'")
                }
            }
        }

        fun has(it: String, type: Type, defaultValue: Value? = null) =
            has(FakeProperty(it, type, defaultValue))

        fun has(it: Type, named: String, defaultValue: Value? = null) =
            has(FakeProperty(named, it, defaultValue))

        infix fun has(fp: FakeProperty): Property {
            if (nextKey == 0) {
                val property = Property(this, fp)
                properties[property.name] = property
                resolve(property.name, property, null)
                return property
            } else {
                throw IllegalStateException("idea with instances cannot 'become'")
            }
        }

        fun hasq(name: String) = name in properties

        /**
         * Nitron constructor; returns a blank instance; never fails
         */
        fun create(): Instance {

            val thing = mind.thing

            // this is a direct instantiation

            // if it is my first instantiation,
            if (nextKey == 0) {

                // then I am concrete, and my instances are particulars
                this `is` thing
            }

            // but if it is not my first instantiation, I must be concrete
            if (this isq thing) {

                // choose a unique key for my new instance
                val k = nextKey++

                // tell the world I have a new instance
                keys.add(k)

                // create that instance
                val instance = Instance(k, this)
                reifications[k] = instance

                for (intension in intensions.values) {

                    val i = intension.idea
                    // this is an indirect instantiation
                    val link = i.nextKey++
                    i.keys.add(link)

                    // setLink
                    i.reifications[link] = instance
                    intension.links[k] = link

                    for (p in i.properties.values) {
                        p[link] = p.defaultValue
                    }
                }

                for (p in properties.values) {
                    p[k] = p.defaultValue
                }

                return instance

            }
            // if I am not, and I am abstract
            else {

                // then I cannot create a particular
                throw RuntimeException("cannot instantiate abstract idea: $name")
            }
        }

        /**
         * Nitron destructor/disengager
         */
        fun destroy(k: Int): Map<String, Value> {

            // make sure it exists first
            assert(k in keys)

            // find its concrete counterpart
            val instance = reifications.remove(k) ?: throw IllegalStateException()

            val type = instance.idea

            // if I'm its concrete idea
            if (this == type) {

                // prepare the seed bundle
                val seed: MutableMap<String, Value> = HashMap()

                // tell the world it no longer exists
                keys.remove(k)

                // only intensions of concrete ideas store links
                for (intension in intensions.values) {

                    val i = intension.idea

                    // removes links
                    val link = intension.links.remove(k)
                    i.keys.remove(link)
                    i.reifications.remove(link)

                    // remove indirect properties
                    for (p in i.properties.values) {
                        val value = p.values.remove(link)
                        if (value != null) {
                            seed[p.owner.name + ":" + p.name] = value
                        }
                    }
                }

                // remove direct properties
                for (p in properties.values) {
                    val value = p.values.remove(k)
                    if (value != null) {
                        seed[p.owner.name] = value
                    }
                }

                return seed

                // otherwise, I'm just an abstract idea for it
            } else {

                // so let its concrete idea handle its destruction
                return type.destroy(instance.index)
            }
        }

        fun <L> generateDocument(language: L): Markup.Document
                where L : Language.Markup, L : Language.HasFileExt {
            return Markup.Document("idea-$name") {
                heading("Idea: ${this@Idea.name}") {
                    paragraph(link("Back to mind", "mind.${language.fileExt}"))
                    heading("Definition") {
                        list {
                            intensions.values.forEach { item(it.fragment) }
                            properties.values.forEach { item(it.fragment) }
                        }
                    }
                    heading("Data") {
                        addSection(toBunch().toTable())
                    }
                }
            }
        }

        fun <L> generateFile(language: L): File
                where L : Language.Markup, L : Language.HasFileExt {
            return language.compile(generateDocument(language), defaultMetadata) writeTo
                    touch("nitron/${mind.name}/" + language.bindFileExt(url))
        }

        fun assertIsq(idea: Idea) {
            assert(this isq idea) { "idea: " + name + " is not: " + idea.name }
        }

        fun toBunch(): Bunch {
            return Bunch(this, HashSet(keys)).apply { sort() }
        }

        override fun toString() = name

        override val typeName = name
        override val fragment = Markup.Tools.link(name, url)

        fun report(): String = toBunch().toTable().toString()

        fun getIntension(name: String) = intensions[name]
        fun getProperty(name: String) = properties[name]
        fun getReifiedValue(key: Int) = reifiedValues[key]
        fun getReification(key: Int) = reifications[key]
        fun getResolution(name: String) = resolutions[name]
        fun overProperties() = Iterable { properties.values.iterator() }
        fun overIntensions() = Iterable { intensions.values.iterator() }

        fun getInstanceTable(index: Int) = Table().apply {
            val address = addColumn<String>("address")
            val value = addColumn<Value>("value")
            for ((name, resolution) in resolutions) {
                this += {
                    address[it] = name
                    value[it] = resolution[index]
                }
            }
        }

        fun getInstanceResolutions(index: Int): Iterable<String> =
            resolutions.keys.toList().filter { resolutions[it]!![index] != null }
    }

    /**
     * Immutable set of instances of one idea; optionally ordered
     *
     * It cannot/must-not contain null values
     */
    data class Bunch(val idea: Idea, private val unsortedKeys: Iterable<Int>) {
        val keys: Iterable<Int>
            get() = sortedKeys ?: unsortedKeys

        private var sortedKeys: List<Int>? = null

        fun sort() {
            sortedKeys = unsortedKeys.toList().sorted()
        }

        /**
         * Returns another Bunch, with only the rows that satisfy a given
         * condition
         */
        fun filter(condition: (Int) -> Boolean) = Bunch(idea, HashSet(keys.filter(condition)))

        /**
         * Does not remove the rows from the table, but returns them as a list
         * of instances
         */
        fun decompress(): List<Instance> =
            keys.map { Instance(it, idea) }

        /**
         * Removes the rows from the table, and returns them as Json.Objects in
         * Json.Array
         */
        fun disengage(): Json.Array =
            Json.Value.of(keys.map { idea.destroy(it).mapValues { it.value.data } }) as Json.Array

        companion object {
            init {
                Json.Serializer.register(
                    Bunch::class, Json.Serializer.Gradual(
                        "idea" to { it.idea.name },
                        "keys" to { it.unsortedKeys.toList() },
                    )
                )
                Json.Deserializer.register(Bunch::class) {
                    Bunch(
                        currentMind.imagine(it["idea"] as String),
                        it["keys"] as List<Int>,
                    )
                }
            }
        }

        fun toTable(): Markup.Table = Table().let { table ->

            val keys = table.addColumn<Int>("#")
            val reifications = table.addColumn<Instance>("@")
            val properties = idea.overProperties().toList()
            val intensions = idea.overIntensions().toList()
            val columnsOfProperties = properties.map { table.addColumn<Value>(it.name) }
            val columnsOfIntensions = intensions.map { table.addColumn<Int>(it.idea.name) }

            for (key in this@Bunch.keys) table += {
                keys[it] = key
                reifications[it] = idea.getReification(key)!!
                for (i in properties.indices) columnsOfProperties[i][it] = properties[i][key]
                for (i in intensions.indices) columnsOfIntensions[i][it] = intensions[i].getLink(key)
            }
            val view = table.view()
            view.toMarkupTable {
                view.forEach { k ->
                    rowHyperdata[k] = "id=\"$k\"" // ${idea.name}_
                }
                makeFragment(reifications) {
                    Markup.Tools.link(it.toString(), it.idea.url, it.index.toString())
                }
                properties.forEachIndexed { i, it ->
                    val c = columnsOfProperties[i]
                    titleHyperdata[c] = "id=\"${it.owner.name}_${it.name}\""
                }
                intensions.forEachIndexed { i, it ->
                    val c = columnsOfIntensions[i]
                    titleFragment[c] = Markup.Tools.link(it.idea.name, it.idea.url)
                    makeFragment(c, idea.mind.makeBookmarker(it.idea))
                }
                view.sortByColumn(keys) { it!! }
            }
        }

        @Suppress("DuplicatedCode") // False positive
        fun upcast(upcastTo: Idea): Bunch {

            // if both ideas are the same, upcasting is unnecessary
            if (this.idea == upcastTo) return this

            // check to see if an upcasting is possible
            this.idea.assertIsq(upcastTo)

            // accumulate the new keys
            val upcastedKeys: MutableSet<Int> = HashSet()
            var lostKeys: MutableSet<Int> = HashSet()

            for (k in unsortedKeys) {
                val q = this.idea.getIntension(upcastTo.name)
                if (q == null) lostKeys.add(k)
                else upcastedKeys.add(q.getLink(k))
            }

            if (lostKeys.isEmpty()) return Bunch(upcastTo, upcastedKeys)
            else throw CastingFailedException(upcastTo, lostKeys)
        }

        @Suppress("DuplicatedCode") // False positive
        fun downcast(downcastTo: Idea): Bunch {

            // if both ideas are the same, downcasting is unnecessary
            if (this.idea == downcastTo) return this

            // check to see if a downcasting is possible
            downcastTo.assertIsq(this.idea)

            // accumulate the new keys
            val downcastedKeys: MutableSet<Int> = HashSet()
            var lostKeys: MutableSet<Int> = HashSet()

            for (k in unsortedKeys) {
                val q = downcastTo.getIntension(this.idea.name)
                if (q == null) lostKeys.add(k)
                else downcastedKeys.add(q.getLink(k))
            }

            if (lostKeys.isEmpty()) return Bunch(downcastTo, downcastedKeys)
            else throw CastingFailedException(downcastTo, lostKeys)
        }

        inner class CastingFailedException(val castTo: Idea, val keys: Set<Int>) :
            Exception("instances were lost during casting")
    }

    data class FakeProperty(
        val name: String,
        val type: Type,
        val defaultValue: Value? = null,
    ) {
        constructor (propertyName: String, default: Value) :
                this(propertyName, default.type(), default)

        constructor (propertyName: String, type: Idea) :
                this(propertyName, type, null)
    }

    data class Property(
        val owner: Idea,
        val name: String,
        val type: Type,
        val defaultValue: Value?,
        val values: MutableMap<Int, Value> = HashMap(),
    ) {
        constructor(owner: Idea, fp: FakeProperty) : this(owner, fp.name, fp.type, fp.defaultValue)

        operator fun get(key: Int) = values[key]
        operator fun set(key: Int, value: Value?) {
            if (value == null) values.remove(key) else values[key] = value
        }

        override fun toString() = "${owner.name}:$name"

        val fragment: Markup.Fragment = Markup.Tools.run {
            bold("has") + " " + link(name, owner.url, owner.name + "_" + name) + " " + bold("as") + " " + type.fragment
        }

        companion object {
            init {
                Json.Serializer.register(
                    Property::class, Json.Serializer.Gradual(
                        "name" to { it.name },
                        "type" to { it.type },
                        "defaultValue" to { it.defaultValue?.data },
                        "values" to { it.values.mapKeys { it.key.toString() }.mapValues { it.value.data } },
                    )
                )
                Json.Deserializer.register(Property::class) {
                    Property(
                        currentIdea,
                        it["name"] as String,
                        it["type"] as Type,
                        Value.of(it["defaultValue"]),
                        (it["values"] as Map<String, Any?>)
                            .mapKeys { it.key.toInt() }.mapValues { Value.of(it.value) }.toMutableMap(),
                    )
                }
            }
        }
    }

    data class Intension(
        val owner: Idea,
        val idea: Idea,
        val links: MutableMap<Int, Int> = HashMap(), // key to key
    ) {
        override fun toString() = "$owner being $idea"

        fun getLink(k: Int): Int {

            // for concrete owners
            return if (k in links) {
                // Human, being concrete, has direct links to its abstract
                // counterparts.
                links[k]!!

                // for abstract owners
            } else {
                // Organism is Physical; but no Organism is directly linked
                // to their Physical counterpart; so we find the reification
                // of the Organism, which is a Human, and has a link to its
                // Physical counterpart.
                val instance = idea.getReification(k)!!
                instance.idea.getIntension(this.idea.name)?.links!![instance.index]!!
            }
        }

        val fragment: Markup.Fragment = Markup.Tools.run {
            bold("is") + " " + link(idea.name, idea.url)
        }

        companion object {
            init {
                Json.Serializer.register(
                    Intension::class, Json.Serializer.Gradual(
                        "idea" to { it.idea.name },
                        "links" to { it.links.mapKeys { it.key.toString() } },
                    )
                )
                Json.Deserializer.register(Intension::class) {
                    Intension(
                        currentIdea,
                        currentMind.imagine(it["idea"] as String),
                        (it["links"] as Map<String, Int>).mapKeys { it.key.toInt() }.toMutableMap(),
                    )
                }
            }
        }
    }

    /**
     * A Nitron runtime, a host for ideas
     */
    data class Mind(
        val name: String,
        private val ideas: MutableMap<String, Idea> = HashMap()
    ) : Iterable<String> {
        override fun iterator(): Iterator<String> = ideas.keys.iterator()
        operator fun contains(ideaName: String) = ideaName in ideas
        operator fun get(ideaName: String) = ideas[ideaName]

        companion object {
            init {
                Json.Serializer.register(
                    Mind::class, Json.Serializer.Gradual(
                        "name" to { it.name },
                        "ideas" to { it.ideas.values },
                    )
                )
                Json.Deserializer.register(Mind::class) {
                    val name = it["name"] as String
                    val ideas: MutableMap<String, Idea> = HashMap()
                    val mind = Mind(name, ideas)
                    mindStack.push(mind)
                    (it["ideas"] as List<Idea>).forEach { ideas[it.name] = it }
                    mindStack.pop()
                }
            }
        }

        //    val nothing: Idea
        val thing: Idea

        override fun hashCode() = name.hashCode()

        override fun equals(other: Any?) = other is Mind && other.name == name

        private val bookmarkers: MutableMap<Idea, (Int?) -> Markup.Fragment> = HashMap()

        fun makeBookmarker(idea: Idea): (Int) -> Markup.Fragment {
            assert(idea.name in ideas)
            return bookmarkers.computeIfAbsent(idea) { { Markup.Tools.link("#$it", idea.url, "$it") } }
        }

        fun imagine(name: String): Idea {
            if (name !in ideas) {
                ideas[name] = Idea(this, name)
            }
            return ideas[name]!!
        }

        fun engage(idea: Idea, seed: Map<String, Value>? = null): Instance {
            val instance = idea.create()
            if (seed != null) {
                instance.initialize(seed)
            }
            return instance
        }

        fun engage(idea: Idea, seed: Set<Pair<String, Value>>? = null): Instance {
            val instance = idea.create()
            if (seed != null) {
                instance.initialize(seed)
            }
            return instance
        }

        fun report(): String {
            var report = name
            for (idea in ideas.values)
                report += "\n" + idea.name + ":\n" + idea.report() + "\n"
            return report
        }

        override fun toString() = "mind:$name"

        fun <L> generateFile(language: L): File
                where L : Language.Markup, L : Language.HasFileExt {
            touch("nitron/$name")
            for (idea in ideas.values) idea.generateFile(language)
            return language.compile(generateDocument(), defaultMetadata) writeTo
                    touch("nitron/$name/mind.${language.fileExt}")
        }

        fun generateDocument() = Markup.Document("mind-$name") {
            heading("Mind: ${this@Mind.name}") {
                heading("Ideas") {
                    addSection(Table().run {
                        val idea = addColumn<Markup.Fragment>("idea")
                        val intensions = addColumn<List<Markup.Fragment>>("intensions")
                        val properties = addColumn<List<Markup.Fragment>>("properties")
                        val instances = addColumn<List<Markup.Fragment>>("instances")

                        for (i in ideas.values) {
                            this += { key ->
                                idea[key] =
                                    Markup.Tools.link(i.name, i.url)
                                intensions[key] =
                                    i.overIntensions().map { Markup.Tools.link(it.idea.name, it.idea.url) }
                                properties[key] =
                                    i.overProperties().map { Markup.Tools.link(it.name, i.url, it.name) }
                                instances[key] =
                                    i.map { Markup.Tools.link("#$it", i.url, it.toString()) }
                            }
                        }

                        toMarkupTable {
                            makeFragment(idea) { it }
                            makeFragment(intensions) { it.joinToFragment { it } }
                            makeFragment(properties) { it.joinToFragment { it } }
                            makeFragment(instances) { it.joinToFragment { it } }
                        }
                    })
                }
                heading("Defaults") {
                    paragraph("Coming soon")
                }
                heading("Convertors") {
                    paragraph("Coming soon")
                }
                heading("Queries") {
                    paragraph("Coming soon")
                }
            }
        }

        init {
//            TODO nothing = imagine("Nothing") // no becoming, no instantiation
            thing = this.imagine("Thing")
            println("Adding mind: $name")
            minds[name] = this
        }
    }

    /**
     * Not a property, but a relative resolution of a property
     * This turns the inheritance nature of Nitron into composition
     */
    class Resolution(val intension: Intension?, val property: Property) {

        // indirect property, relative resolution ?: direct property, absolute resolution
        fun absolveKey(key: Int): Int {
            return intension?.getLink(key) ?: key
        }

        operator fun set(key: Int, value: Value?) {
            property[absolveKey(key)] = value
        }

        operator fun get(key: Int): Value? {
            return property[absolveKey(key)]
        }

        override fun toString() = "~" + (intension?.idea?.name ?: "") + ":" + property.name
    }

    infix fun String.`as`(type: Type) = FakeProperty(this, type)
    infix fun Type.named(name: String) = FakeProperty(name, this)
}