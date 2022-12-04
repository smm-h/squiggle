package ir.smmh.lisp

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language
import ir.smmh.lingu.Tokenizer
import ir.smmh.nilex.NiLexLanguage.FilteredTokens
import ir.smmh.nilex.NiLexLanguage.assertBalance
import ir.smmh.nilex.NiLexLanguage.filterOut
import ir.smmh.nilex.NiLexTokenizer.Companion.v
import ir.smmh.nilex.NiLexTokenizerFactory
import java.io.File

class Lisp(customize: Customization.() -> Unit) : Language.Construction<Runnable> {

    private val customization = CustomizationImpl().apply(customize)

    interface Customization {
        fun interface NamedValues {
            fun nameAll(name: (String, Value) -> Unit)
        }

        fun name(name: String, value: Value)
        fun nameAll(it: NamedValues) = it.nameAll(::name)

        fun setBrackets(opener: String, closer: String, f: Value.f)
        fun setSquareBrackets(f: Value.f) = setBrackets("[", "]", f)
        fun setCurlyBraces(f: Value.f) = setBrackets("{", "}", f)
        fun setArrowBrackets(f: Value.f) = setBrackets("<", ">", f)

        fun setBrackets(opener: String, closer: String, name: String)
        fun setSquareBrackets(name: String) = setBrackets("[", "]", name)
        fun setCurlyBraces(name: String) = setBrackets("{", "}", name)
        fun setArrowBrackets(name: String) = setBrackets("<", ">", name)
    }

    private class CustomizationImpl : Customization {
        private val verbatims = HashSet<String>().apply { add(v("(")); add(v(")")) }
        val openers = HashMap<String, Value.f>()
        val closers = HashSet<String>().apply { add(v(")")) }
        var assertAreBalanced = Code.Process.empty
        val appendix = StringBuilder()
        val values = ArrayList<Pair<String, Value>>()
        val valueOfName = HashMap<String, Value>()
        val namedValues = HashMap<Value, String>()

        override fun name(name: String, value: Value) {
            values.add(name to value)
            valueOfName[name] = value
            namedValues[value] = name
        }

        fun setAll(set: (String, Value) -> Unit) {
            for ((name, value) in values)
                set(name, value)
        }

        override fun setBrackets(opener: String, closer: String, name: String) =
            setBrackets(opener, closer, valueOfName[name] as Value.f)

        override fun setBrackets(opener: String, closer: String, f: Value.f) {
            if (opener in verbatims || closer in verbatims)
                throw Exception("brackets must not clash")
            val o = v(opener)
            val c = v(closer)
            verbatims += o
            verbatims += c
            openers += o to f;
            closers += c
            assertAreBalanced += assertBalance(o, c, FilteredTokens)
            appendix.append("verbatim '$opener' verbatim '$closer'")
        }
    }

    override val construction = Code.Aspect<Runnable>("root-block")

    private val tokenize: Tokenizer = NiLexTokenizerFactory.create(
        """
        streak '\t\n\r ' as whitespace
        streak '[0-9]' as digits
        streak '[A-Z][a-z][0-9]_' as id
        keep '"' ... '"' as string
        keep '//' ... '\n' as comment
        keep '/*' ... '*/' as multiLineComment
        verbatim '('
        verbatim ')'
        """ + customization.appendix
    )

    private val tabLength = 4
    private val space = " ".repeat(tabLength)
    private val dash = "─".repeat(tabLength - 1)

    private inner class Callstack {

        private fun valueToString(value: Value): String {
            return customization.namedValues[value] ?: value.toString()
        }

        private var depth: Int = 1
        fun printIndented(string: String) {
            print(space.repeat(depth))
            println(string)
        }

        private var currentFrame: Frame? = Frame(null).apply {
            customization.setAll(::set)
            appendToArguments(this["block"]?.value!!)
        }

        fun push() {
            printIndented("└$dash┐")
            depth++
            currentFrame = Frame(currentFrame)
        }

        fun appendToArguments(value: Value) {
            currentFrame!!.appendToArguments(value)
        }

        fun pop(): Value? {
            val previousFrame = currentFrame!!
            currentFrame = previousFrame.parent
            depth--
            printIndented("┌$dash┘")
            return previousFrame.evaluate()
        }

        private inner class Variable(val type: Type) {
            var value: Value = Value._nothing
                set(v: Value) {
                    if (v.type.isSubtypeOf(type)) {
                        field = v
                    } else {
                        printIndented("╞ setting failed")
                    }
                }
        }

        operator fun get(name: String): Value? {
            return currentFrame!![name]?.value
        }

        private inner class Frame(val parent: Frame?) {
            private val _this: MutableMap<String, Variable> = HashMap()

            /**
             * Searches for a variable with the given name in the current frame,
             * or the nearest parent, and returns it.
             */
            operator fun get(name: String): Variable? {
                return _this[name] ?: parent?.get(name)
            }

            operator fun set(name: String, type: Type) {
                _this[name] = Variable(type)
            }

            /**
             * Procures a variable by either searching for an existing one in
             * the frame and its parents, or creating one in the frame, and then
             * set the given value to that variable.
             */
            operator fun set(name: String, value: Value) {
                val variable = this[name] ?: Variable(Type._Anything).also { _this[name] = it }
                variable.value = value
            }

            private val arguments = ArrayDeque<Value>()
            fun appendToArguments(value: Value) {
                arguments.addLast(value)
                printIndented("├ " + valueToString(value))
            }

            fun evaluate(): Value? {
                val headValue = arguments.removeFirst()
                val headType = headValue.type
                if (headType is Type._Callable) {
                    if (headType == Type._Callable.Tail) {
                        printIndented("╞ head cannot be tail")
                        return null
                    }
                    val argumentTypes: List<Type> = arguments.map(Value::type)
//                        { it.type.run { if (this == Type._UndefinedVariable) Type._Anything else this } }
                    val argumentsResult = headType.checkArgumentTypes(argumentTypes)
                    if (argumentsResult == null) {
                        val returnValue = (headValue as Value.f).callable.call(arguments)
                        val returnType = returnValue.type
                        val returnResult = headType.checkReturnType(returnType)
                        if (returnResult == null) {
//                            printIndented("> " + valueToString(returnValue))
                            return returnValue
                        } else printIndented("╞ bad output: ${valueToString(returnValue)}; $returnResult")
                    } else printIndented("╞ bad input: ${argumentTypes.map(::valueToString)}; $argumentsResult")
                } else printIndented("╞ value is not callable: ${valueToString(headValue)}")
                return null
            }
        }
    }

    override val process: Code.Process = tokenize +
            filterOut("opener", "closer", "whitespace", "comment", "multiLineComment") +
            customization.assertAreBalanced + { code ->
        val queue = ArrayDeque((FilteredTokens of code)!!)
        val stack = Callstack()
        while (queue.isNotEmpty()) {
            val token = queue.removeFirst()
            val typeName = token.type.name
            if (typeName == v("(")) {
                stack.push()
            } else if (typeName in customization.openers) {
                stack.push()
                stack.appendToArguments(customization.openers[typeName]!!)
            } else {
                val value: Value? = if (typeName in customization.closers) {
                    stack.pop()
                } else when (typeName) {
                    "digits" -> Value._number(token.data.toDouble())
                    "string" -> Value._string(token.data)
                    "id" -> {
                        val id = token.data
                        stack[id] ?: Value._undefined(id)
                    }
                    else -> {
                        code.issue(token, "unknown token type $typeName")
                        null
                    }
                }
                if (value != null) stack.appendToArguments(value)
            }
        }
        code[construction] = Runnable {
            (stack.pop() as Value.f).callable.call(emptyList())
        }
    }

    companion object {
        val defaultFlavor = Lisp {
            nameAll(Type.namedValues)
            nameAll(Value.namedValues)
            nameAll(Type._Callable.namedValues)
            setCurlyBraces("block")
            // setSquareBrackets("list")
            setArrowBrackets("argumentsMaker")
        }

        @JvmStatic
        fun main(args: Array<String>) {
            Lisp.defaultFlavor.code(File("res/lisp-test")).beConstructedInto<Runnable>().run()
        }
    }
}