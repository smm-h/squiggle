//@file:Suppress("UNCHECKED_CAST", "ObjectPropertyName")
//
//package ir.smmh.nitron
//
//import ir.smmh.lingu.Code.Companion.links
//import ir.smmh.lingu.tokenization.nilexer.NiLex.UnknownChar.name
//import kotlin.collections.EmptyMap.keys
//
//private val _Bunch = buildBundleSerializer("Bunch", {
//    Nitron.Bunch(
//        Nitron.contexts.peek().mind.imagine(it["idea"]!! as String),
//        it["keys"]!! as List<Int>,
//    )
//}) {
//    addElement("idea", String.serializer(), { idea.name })
//    addElement("keys", ListSerializer(Int.serializer()), { keys.toList() })
//}
//
//private val _Property = buildBundleSerializer("Property", {
//    Property(
//        Nitron.contexts.peek().idea.peek() as Nitron.Idea,
//        it["name"]!! as String,
//        Value.Type.serializer.toOriginal(it["type"]!! as String),
//        (it["defaultValue"] as Value.Intermediate?)?.toOriginal(),
//    )
//}) {
//    addElement("name", String.serializer(), { name })
//    addElement("type", String.serializer(), { type.toIntermediate() })
//    addElement(
//        "defaultValue",
//        Value.Intermediate.serializer(),
//        { defaultValue?.toIntermediate() },
//        isNullable = true
//    )
//}
//
//private val _Intension = buildBundleSerializer("Intension", {
//    Nitron.Intension(
//        Nitron.contexts.peek().idea.peek() as Nitron.Idea,
//        Nitron.contexts.peek().mind.imagine(it["idea"]!! as String),
//        (it["links"]!! as Map<Int, Int>).toMutableMap(),
//    )
//}) {
//    addElement("idea", String.serializer(), { idea.name })
//    addElement("links", MapSerializer(Int.serializer(), Int.serializer()), { links })
//}
//
//private val _Resolution = buildBundleSerializer("Resolution", {
//    Nitron.Resolution(
//        it["intension"] as Intension?,
//        it["property"]!! as Property,
//    )
//}) {
//    addElement("intension", _Intension, { intension }, isNullable = true)
//    addElement("property", _Property, { property })
//}
//
//private val _Idea = buildBundleSerializer("Idea", {
//    // POP IDEA
//    Nitron.contexts.peek().idea.pop()
//}) {
//    addElement("name", String.serializer(), { name }) {
//        val mind = Nitron.contexts.peek().mind
//        val ideaName = it["name"] as String
//        val idea = mind.imagine(ideaName)
//        // PUSH IDEA
//        Nitron.contexts.peek().idea.push(idea)
//    }
//    addElement("keys", ListSerializer(Int.serializer()), { keys }) {
//        val idea = Nitron.contexts.peek().idea.peek()!!
//        val keys = it["keys"] as List<Int>
//        idea.keys.addAll(keys)
//    }
//    addElement("intensions", ListSerializer(_Intension), { intensions.values.toList() }) {
//        val idea = Nitron.contexts.peek().idea.peek()!!
//        val intensions = (it["intensions"]!! as List<Intension>).associateBy { i -> i.idea.name }
//        idea.intensions.putAll(intensions)
//    }
//    addElement("properties", ListSerializer(_Property), { properties.values.toList() }) {
//        val idea = Nitron.contexts.peek().idea.peek()!!
//        val properties = (it["properties"]!! as List<Property>).associateBy { i -> i.type.identifier }
//        idea.properties.putAll(properties)
//    }
//    addElement("nextKey", Int.serializer(), { nextKey }) {
//        val idea = Nitron.contexts.peek().idea.peek()!!
//        val nextKey = it["nextKey"]!! as Int
//        idea.nextKey = nextKey
//    }
//    addElement("reifiedValues", MapSerializer(Int.serializer(), Value.serializer), { reifiedValues }) {
//        val idea = Nitron.contexts.peek().idea.peek()!!
//        val reifiedValues = it["reifiedValues"]!! as Map<Int, Value>
//        idea.reifiedValues.putAll(reifiedValues)
//    }
//    addElement("reifications", MapSerializer(Int.serializer(), Value.serializer), { reifications }) {
//        val idea = Nitron.contexts.peek().idea.peek()!!
//        val reifications = it["reifications"]!! as Map<Int, Instance>
//        idea.reifications.putAll(reifications)
//    }
//    addElement("resolutions", MapSerializer(String.serializer(), _Resolution), { resolutions }) {
//        val idea = Nitron.contexts.peek().idea.peek()!!
//        val resolutions = it["resolutions"]!! as Map<String, Nitron.Resolution>
//        idea.resolutions.putAll(resolutions)
//    }
//}
//
//private val _Mind = buildBundleSerializer("Mind", {
//    // return it and go back
//    Nitron.contexts.pop().mind
//}) {
//    addElement("name", String.serializer(), { name }) {
//        // create and go-to mind
//        val mindName = it["name"] as String
//        Nitron.contexts.push(Nitron.Context(Nitron.Mind(mindName)))
//    }
//    addElement("ideas", ListSerializer(_Idea), { ideas.values.toList() })// {
////        // add ideas to it while it is in pack
////        pack.peek().mind.apply {
////            val ideas = it["ideas"]!! as Map<String, Idea>
////            for (ideaName in ideas.keys) {
////                imagine(ideaName)
////            }
////            for (ideaName in ideas.keys) {
////                getIdea(ideaName)
////            }
////        }
////    }
//}