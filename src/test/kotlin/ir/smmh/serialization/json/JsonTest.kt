package ir.smmh.serialization.json

import ir.smmh.serialization.json.Json.classifierKey
import ir.smmh.util.SecurityUtil.hash
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.PI
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


object JsonTest {

    fun getBasics() = Json.Value.of(
        mapOf(
            "k-?" to null,
            "k-t" to true,
            "k-f" to false,
            "k-n" to -123.456,
            "k-s" to "string",
            "k-[]" to listOf(1, 2, 3),
            "k-{}" to mapOf(
                "a" to 65,
                "b" to 66,
            ),
        )
    ) as Json.Object.Mutable

    @Test
    fun testBasics() {
        val v = getBasics()
        assertEquals(
            """{"k-?":null,"k-t":true,"k-f":false,"k-n":-123.456,"k-s":"string","k-[]":[1,2,3],"k-{}":{"a":65,"b":66}}""",
            v.serialization
        )
        assertEquals(
            "{k-?=null, k-t=true, k-f=false, k-n=-123.456, k-s=string, k-[]=[1, 2, 3], k-{}={a=65, b=66}}",
            v.deserialization.toString()
        )
        assertEquals("[1, 2, 3]", v["k-[]"].toString())
        assertEquals("{a=65, b=66}", v["k-{}"].toString())
        assertEquals("true", v["k-t"].toString())

        v["k-[]"] = "no longer a list"
        assertEquals(
            """{"k-?":null,"k-t":true,"k-f":false,"k-n":-123.456,"k-s":"string","k-[]":"no longer a list","k-{}":{"a":65,"b":66}}""",
            v.serialization
        )
        assertEquals(
            "{k-?=null, k-t=true, k-f=false, k-n=-123.456, k-s=string, k-[]=no longer a list, k-{}={a=65, b=66}}",
            v.deserialization.toString()
        )
        assertEquals("no longer a list", v["k-[]"].toString())
    }

    @Test
    fun testQuerying() {
        val v = getBasics()
        assertEquals(3, v.query("k-[]/2").deserialization)
        assertEquals(66, v.query("k-{}/b").deserialization)
    }

    fun getEnum() = Json.Value.of(
        mapOf(
            "some key" to "some data",
            "black-dog" to Dog.BLACK,
            "black-cat" to Cat.BLACK,
            "his-pets" to listOf(
                Dog.GOLDEN, Cat.ORANGE, Cat.ORANGE, Dog.WHITE, Dog.BLACK
            ),
            "her-pets" to listOf(
                Dog.SMALL, Cat.WHITE, Cat.GRAY
            )
        )
    ) as Json.Object

    enum class Dog : Json.Enum {
        GOLDEN, BLACK, WHITE, SMALL;

        override val identifier = "dog"
    }

    enum class Cat : Json.Enum {
        ORANGE, BLACK, WHITE, GRAY;

        override val identifier = "cat"
    }

    @Test
    fun testEnum() {

        val v = getEnum()

        assertEquals(
            """Json.Object:{some key=Json.String:some data, black-dog=Json.String:BLACK, black-cat=Json.String:BLACK, his-pets=Json.Array:[Json.String:GOLDEN, Json.String:ORANGE, Json.String:ORANGE, Json.String:WHITE, Json.String:BLACK], her-pets=Json.Array:[Json.String:SMALL, Json.String:WHITE, Json.String:GRAY]}""",
            v.toString()
        )
        assertEquals(
            """{"some key":"some data","black-dog":"BLACK@dog","black-cat":"BLACK@cat","his-pets":["GOLDEN@dog","ORANGE@cat","ORANGE@cat","WHITE@dog","BLACK@dog"],"her-pets":["SMALL@dog","WHITE@cat","GRAY@cat"]}""",
            v.serialization
        )
        assertEquals(
            """{some key=some data, black-dog=BLACK, black-cat=BLACK, his-pets=[GOLDEN, ORANGE, ORANGE, WHITE, BLACK], her-pets=[SMALL, WHITE, GRAY]}""",
            v.deserialization.toString()
        )
        assertEquals(Dog.BLACK, v["black-dog"])
    }

    @Test
    fun testListeners() {
        val v = connect("password", "password_hash") {
            (it as String).hash("sha-256")
        }
//        println(v.serialization)

        v["username"] = "u"
        v["password"] = "p1"
//        println(v.serialization)
        val h1 = v["password_hash"] as String

        // we only change the password, but password_hash changes as well
        v["password"] = "p2"
//        println(v.serialization)
        val h2 = v["password_hash"] as String

        assertNotEquals(h1, h2)
    }

    fun connect(
        sourceKey: String,
        destinationKey: String,
        convert: (Any?) -> Any?
    ): Json.Object.Mutable = Json.Object.Mutable.empty().apply {

        val oldHashCode = AtomicInteger()

        mut.onPreMutate.add {
            // before it mutates, keep the hashCode of the old value
            oldHashCode.set(this[sourceKey].hashCode())
        }

        mut.onMutate.add {
            // after it has mutated, compare the old hashCode with the new
            val sourceData = this[sourceKey]
            val newHashCode = sourceData.hashCode()

            // if they do not match, the value has changed
            if (oldHashCode.get() != newHashCode) {

                // so change the connected value
                this[destinationKey] = convert(sourceData)
                oldHashCode.set(newHashCode)
            }
        }
    }

    val sample = """{"a":[500,  true,  false   , null, -9e-5],"b":[  {} , [],   "nu"  ]}"""

    @Test
    fun testParsing() {
        val v = Json.parse(sample)
        assertEquals(
            """Json.Object:{"a":[500,  true,  false   , null, -9e-5],"b":[  {} , [],   "nu"  ]}""",
            v.toString()
        )
        assertEquals(
            """{"a":[500,  true,  false   , null, -9e-5],"b":[  {} , [],   "nu"  ]}""",
            v.serialization
        )
        assertEquals(
            """{a=[500, true, false, null, -1.6935087808430286E-5], b=[{}, [], nu]}""",
            v.deserialization.toString()
        )
    }

    @Test
    fun testClassifying() {
        val a1 = TestClass(25, "twenty-five")
        val classifier = "JsonTest.TestClass"
        val s = """{"int":${a1.int},"string":"${a1.string}","$classifierKey":"$classifier"}"""
        val d = "{${a1.int},${a1.string}}"
        val v = "Json.Object:{int=Json.Number:${a1.int}, string=Json.String:${a1.string}, " +
                "$classifierKey=Json.String:$classifier}"
        val r = """Json.Object:{"int":${a1.int},"string":"${a1.string}","$classifierKey":"$classifier"}"""
        assertEquals(d, a1.toString())
        Json.Value.of(a1).apply {
            val a2 = deserialization
            assertEquals(v, toString())
            assertEquals(s, serialization)
            assertEquals(d, a2.toString())
            Json.parse(serialization).apply {
                val a3 = deserialization
                assertEquals(r, toString())
                assertEquals(s, serialization)
                assertEquals(d, a3.toString())

                // prove that a1, a2, and a3 are three different objects
                assertEquals(3, listOf(a1, a2, a3).map { it.hashCode() }.toSet().size)

                // but their toString is the same
                assertEquals(1, listOf(a1, a2, a3).map { it.toString() }.toSet().size)
            }
        }
    }

    class TestClass(
        val int: Int,
        val string: String
    ) {
        companion object {
            init {
                Json.Serializer.register(
                    TestClass::class, Json.Serializer.Gradual(
                        "int" to { it.int },
                        "string" to { it.string },
                    )
                )
                Json.Deserializer.register(TestClass::class) {
                    TestClass(
                        it["int"] as Int,
                        it["string"] as String
                    )
                }
            }
        }

        override fun toString() = "{$int,$string}"
    }

    @Test
    fun testClassifyingVar() {
        val a1 = TestClassVar(25, "twenty-five")
        val classifier = "JsonTest.TestClassVar"
        val s = """{"int":${a1.int},"string":"${a1.string}","$classifierKey":"$classifier"}"""
        val d = "{${a1.int},${a1.string}}"
        val v = "Json.Object:{int=Json.Number:${a1.int}, string=Json.String:${a1.string}, " +
                "$classifierKey=Json.String:$classifier}"
        val r = """Json.Object:{"int":${a1.int},"string":"${a1.string}","$classifierKey":"$classifier"}"""
        assertEquals(d, a1.toString())
        Json.Value.of(a1).apply {
            val a2 = deserialization
            assertEquals(v, toString())
            assertEquals(s, serialization)
            assertEquals(d, a2.toString())
            Json.parse(serialization).apply {
                val a3 = deserialization
                assertEquals(r, toString())
                assertEquals(s, serialization)
                assertEquals(d, a3.toString())

                // prove that a1, a2, and a3 are three different objects
                assertEquals(3, listOf(a1, a2, a3).map { it.hashCode() }.toSet().size)

                // but their toString is the same
                assertEquals(1, listOf(a1, a2, a3).map { it.toString() }.toSet().size)
            }
        }
    }

    class TestClassVar(
        var int: Int = 0,
        var string: String = "",
    ) {

        companion object {
            val both = Json.SerializerAndDeserializer.Gradual(
                Triple("int", { it.int }, { int = it as Int }),
                Triple("string", { it.string }, { string = it as String }),
            ) { TestClassVar() }

            init {
                Json.SerializerAndDeserializer.registerBoth(TestClassVar::class, both)
            }
        }

        override fun toString() = "{$int,$string}"
    }

    @Test
    fun testArrayIteration() {
        val array = Json.Array.Mutable.empty().apply {
            add(null)
            add(true)
            add(false)
            add(5.0)
            add(-8)
            add(PI)
            add("Hello, Json!")
            add("")
            add("\n\"")
            add(Json.Array.Mutable.empty())
            add(Json.Array.Mutable.empty().also { it.add("Hey!") })
            add(Json.Object.Mutable.empty())
            add(Json.Object.Mutable.empty().also { it.setAtPlace("Some key!", "Hey!") })
        }
        println(array)
        array.forEach {
            println(it)
        }
    }
}