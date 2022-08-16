package ir.smmh.nitron

import ir.smmh.lingu.Code
import ir.smmh.lingu.Language.Companion.lateFileExt
import ir.smmh.lingu.Splitter
import ir.smmh.lingu.TokenizationUtil.Exception
import ir.smmh.lingu.Tokenizer
import ir.smmh.lingu.Tokenizer.Companion.Tokens
import ir.smmh.markup.Html
import ir.smmh.markup.toOnOff
import ir.smmh.markup.toYesNo
import ir.smmh.nitron.Nitron.Idea
import ir.smmh.nitron.Nitron.Instance
import ir.smmh.nitron.Nitron.Mind
import ir.smmh.nitron.Nitron.Value
import ir.smmh.nitron.Nitron.`as`
import ir.smmh.serialization.json.Json
import ir.smmh.util.FileUtil.open
import ir.smmh.util.FileUtil.touch
import java.io.File
import java.util.*

object CLI {

    enum class State {
        HOME, MIND, IDEA, INSTANCE
    }

    private var state: State = State.HOME
    private var mindName: String? = null
    private var ideaName: String? = null
    private var instanceKey: Int? = null
    private var autoReport: Boolean = true
    private var autoSave: Boolean = true

    private fun mutate() {
        if (autoReport) report()
        if (autoSave) save()
    }

    private fun all() {
        val iterable: Iterable<Any> = when (state) {
            State.HOME -> Nitron
            State.MIND -> mind
            State.IDEA -> idea
            State.INSTANCE -> instance
        }
        println(iterable.joinToString())
    }

    private fun exists(it: String) {
        val exists: Boolean = when (state) {
            State.HOME -> it in Nitron
            State.MIND -> it in mind
            State.IDEA -> it.toInt() in idea
            State.INSTANCE -> instance[it] == null
        }
        println(exists.toYesNo())
    }

    private fun toggleAutoReport() {
        autoReport = !autoReport
        println("Auto-report is now ${autoReport.toOnOff()}")
    }

    private fun toggleAutoSave() {
        autoSave = !autoSave
        println("Auto-save is now ${autoSave.toOnOff()}")
    }

    private fun goBack() {
        when (state) {
            State.HOME -> println("Use 'exit' to exit")
            State.MIND -> goHome()
            State.IDEA -> {
                state = State.MIND
                ideaName = null
                instanceKey = null
            }
            State.INSTANCE -> {
                state = State.IDEA
                instanceKey = null
            }
        }
    }

    private fun goHome() {
        state = State.HOME
        mindName = null
        ideaName = null
        instanceKey = null
    }

    fun goToMind(mind: Mind) {
        goToMind(mind.name)
    }

    private fun goToMind(name: String) {
        state = State.MIND
        mindName = name
        ideaName = null
        instanceKey = null
    }

    private fun goToIdea(name: String) {
        state = State.IDEA
        ideaName = name
        instanceKey = null
    }

    private fun goToInstance(key: Int) {
        state = State.INSTANCE
        instanceKey = key
    }

    private fun getMindFile(mindName: String) =
        File(touch(Json.bindFileExt("nitron/$mindName/mind.$lateFileExt")))

    private val mind: Mind get() = Nitron[mindName!!]!!
    private val idea: Idea get() = mind.imagine(ideaName!!)
    private val instance: Instance get() = Instance(instanceKey!!, idea)

    private fun getAddress(): String {
        return when (state) {
            State.HOME -> ""
            State.MIND -> "$mindName"
            State.IDEA -> "$mindName@$ideaName"
            State.INSTANCE -> "$mindName@$ideaName#$instanceKey"
        }
    }

    private fun findIdea(name: String): Idea {
        return mind.imagine(name)
    }

    private fun save() {
        getMindFile(mindName!!).writeText(Json.serialize(mind))
        println("Saved progress...")
    }

    private fun report() {
        println(
            when (state) {
                State.HOME -> "Select something to report on"
                State.MIND -> mind.report()
                State.IDEA -> idea.report()
                State.INSTANCE -> instance.report()
            }
        )
    }

    private fun preview() {
        when (state) {
            State.HOME -> println("Select something to preview")
            State.MIND -> mind.generateFile(Html) open ""
            State.IDEA -> idea.generateFile(Html) open ""
            State.INSTANCE -> instance.generateFile(Html) open ""
        }
    }

    private val commands: Map<State?, Set<String>> = mapOf(
        null to setOf(
            "all",
            "exists",
            "toggleAutoReport",
            "toggleAutoSave",
            "home",
            "back",
            "save",
            "report",
            "preview",
            "help",
            "exit"
        ),
        State.HOME to setOf(
            "load",
        ),
        State.MIND to setOf(
            "imagine",
        ),
        State.IDEA to setOf(
            "is",
            "has",
            "create",
            "edit",
            "is?",
        ),
        State.INSTANCE to setOf(
            "get",
            "set",
            "is?",
        ),
    )

    private fun help(command: String?) {
        println("Your current state is: ${state.name}")
        when (command) {
            null -> {
                println("Commands you can invoke in any state:")
                println(commands[null])
                println("Commands you can only invoke in this state:")
                println(commands[state])
                println("Use 'help' with a command to show information about that command")
            }
            "all" -> {
                println("Print a list of all the things in the current thing")
                println("You can invoke this command in any state")
                println("No arguments")
            }
            "exists" -> {
                println("Check to see if something exists in the current thing")
                println("You can invoke this command in any state")
                println("Arguments: name")
            }
            "toggleAutoReport" -> {
                println("Toggle seeing the table after every change")
                println("You can invoke this command in any state")
                println("No arguments")
            }
            "toggleAutoSave" -> {
                println("Toggle saving the contents of the mind after every change")
                println("You can invoke this command in any state")
                println("No arguments")
            }
            "home" -> {
                println("Go to the HOME state")
                println("You can invoke this command in any state")
                println("No arguments")
            }
            "back" -> {
                println("Go up one state")
                println("You can invoke this command in any state")
                println("No arguments")
            }
            "save" -> {
                println("Save the current mind to file")
                println("You can invoke this command in any state")
                println("No arguments")
            }
            "report" -> {
                println("Print the contents to the terminal")
                println("You can invoke this command in any state")
                println("No arguments")
            }
            "preview" -> {
                println("Show the contents in the browser")
                println("You can invoke this command in any state")
                println("No arguments")
            }
            "help" -> {
                println("Show help messages")
                println("You can invoke this command in any state")
                println("Arguments: command")
            }
            "exit" -> {
                println("Stop the CLI")
                println("You can invoke this command in any state")
                println("No arguments")
            }
            "load" -> {
                println("Load a mind from file, or create a new one, with the given name")
                println("You can only invoke this command in the HOME state")
                println("Arguments: name")
            }
            "imagine" -> {
                println("Create a new idea with a given name")
                println("You can only invoke this command in the MIND state")
                println("Arguments: name")
            }
            "is" -> {
                println("Add an intension")
                println("You can only invoke this command in the IDEA state")
                println("Arguments: intension")
            }
            "has" -> {
                println("Add a property")
                println("You can only invoke this command in the IDEA state")
                println("Arguments: name, type")
            }
            "create" -> {
                println("Creates a new instance")
                println("You can only invoke this command in the IDEA state")
                println("No arguments")
            }
            "edit" -> {
                println("Edit an instance")
                println("You can only invoke this command in the IDEA state")
                println("Arguments: index")
            }
            "is?" -> {
                println("Check whether an intension exists in the selected idea or instance")
                println("You can only invoke this command in the IDEA or INSTANCE state")
                println("Arguments: intension")
            }
            "get" -> {
                println("Print the value for a property of the selected instance")
                println("You can only invoke this command in the INSTANCE state")
                println("Arguments: name")
            }
            "set" -> {
                println("Assign the value for a property of the selected instance")
                println("You can only invoke this command in the INSTANCE state")
                println("Arguments: name, value")
            }
            else -> {
                println("No such command; try 'help' to see available commands")
            }
        }
    }

    fun start() {
        val tokenizer: Tokenizer = Splitter.Predefined.splitter
        Scanner(System.`in`).use {
            while (true) {
                try {
                    print(getAddress())
                    print(" ~> ")
                    val code = Code(it.nextLine(), null)
                    tokenizer.tokenize(code)
                    val tokens = (Tokens of code)!!
                    val command = tokens[0].data
                    var notDoneYet = true
                    @Suppress("KotlinConstantConditions") // this way is more stylish
                    if (notDoneYet) {
                        notDoneYet = false
                        when (command) {
                            "all" -> all()
                            "exists" -> exists(tokens[1].data)
                            "toggleAutoReport" -> toggleAutoReport()
                            "toggleAutoSave" -> toggleAutoSave()
                            "home" -> goHome()
                            "back" -> goBack()
                            "save" -> save()
                            "report" -> report()
                            "preview" -> preview()
                            "help" -> help(if (tokens.size == 1) null else tokens[1].data)
                            "exit" -> break
                            else -> notDoneYet = true
                        }
                    }
                    if (notDoneYet) {
                        notDoneYet = false
                        when (state) {
                            State.HOME -> when (command) {
                                "load" -> {
                                    val name = tokens[1].data
                                    if (name !in Nitron) {
                                        val file = getMindFile(name)
                                        if (file.exists()) {
                                            Json.deserialize(file.readText())
                                        } else {
                                            println("Mind not found, creating it")
                                            touch("nitron/$name")
                                            Mind(name)
                                        }
                                    }
                                    goToMind(name)
                                }
                                else -> {
                                    if (command in Nitron) goToMind(command)
                                    else notDoneYet = true
                                }
                            }
                            State.MIND -> when (command) {
                                "imagine" -> {
                                    val ideaName = tokens[1].data
                                    if (ideaName !in mind) {
                                        mind.imagine(ideaName)
                                        mutate()
                                    }
                                    goToIdea(ideaName)
                                }
                                else -> {
                                    if (command in mind) goToIdea(command)
                                    else notDoneYet = true
                                }
                            }
                            State.IDEA -> when (command) {
                                "is" -> {
                                    val intension: String = tokens[1].data
                                    if (idea.isq(findIdea(intension))) {
                                        println("Already is")
                                    } else {
                                        idea.`is`(findIdea(intension))
                                        mutate()
                                    }
                                }
                                "has" -> {
                                    val name: String = tokens[1].data
                                    val type: String = tokens[2].data
                                    val idea = idea
                                    if (idea.hasq(name)) {
                                        println("Property already exists")
                                    } else {
                                        idea has (name `as` findIdea(type))
                                        mutate()
                                    }
                                }
                                "create" -> {
//                                    val values = Json.deserialize(tokens[1].data) as Map<String, Value>
                                    val instance = idea.create()
                                    // TODO instance.initialize(values)
                                    goToInstance(instance.index)
                                }
                                "edit" -> {
                                    val token = tokens[1]
                                    if (token.type == Splitter.Predefined.NUMBER) {
                                        val index: Int = token.data.toInt()
                                        goToInstance(index)
                                    } else {
                                        notDoneYet = true
                                    }
                                }
                                "is?" -> {
                                    val intension: String = tokens[1].data
                                    println((idea isq findIdea(intension)).toYesNo())
                                }
                                else -> {
                                    val index = command.toIntOrNull()
                                    if (index != null && index in idea) goToInstance(index)
                                    else notDoneYet = true
                                }
                            }
                            State.INSTANCE -> when (command) {
                                "get" -> {
                                    val propertyName: String = tokens[1].data
                                    println(instance.get(propertyName))
                                }
                                "set" -> {
                                    val propertyName: String = tokens[1].data
                                    val value = Value.of(Json.deserialize(tokens[2].data))
                                    instance.set(propertyName, value)
                                }
                                "is?" -> {
                                    val intension: String = tokens[1].data
                                    val instance: Instance = instance
                                    println((instance isq findIdea(intension)).toYesNo())
                                }
                                else -> {
                                    if (command in instance) println(instance.get(command))
                                    else notDoneYet = true
                                }
                            }
                        }
                    }
                    if (notDoneYet) {
                        println("Unknown command: $command")
                    }
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
    }
}