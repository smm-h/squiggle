package ir.smmh.mage.core

import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.roundToLong

abstract class App(val platform: Platform) {

    val temporalRoot: Temporal.Group<Temporal> = Temporal.Group.List()
    val visualRoot: Visual.Group<Visual> = Visual.Group.List()

    private val eventQueue: Queue<Event.Happened<*>> = ConcurrentLinkedQueue()
    private val eventFilter: MutableMap<Event<*>, MutableList<(Any) -> Unit>> = ConcurrentHashMap()

    val process: Process = platform.createProcess(::dispatch, visualRoot::draw)

    var title: String by process::title
    var size: Size by process::size

    private val finalizables = Stack<WeakReference<Finalizable>>()

    fun finally(finalizable: Finalizable) {
        finalizables.push(WeakReference(finalizable))
    }

    @Suppress("MemberVisibilityCanBePrivate")
    @Override
    protected fun finalize() {
        while (finalizables.isNotEmpty())
            finalizables.pop().get()?.finalize()
    }

    private var running: Boolean = true
    private var restart: Boolean = true
    private var timeout: Long = 0
    var fps: Double
        get() = 1000.0 / timeout
        set(value) {
            timeout = (1000.0 / value).roundToLong()
        }

    // modify state
    fun restart() {
        restart = true
    }

    fun exit() {
        running = false
    }

    fun dispatch(happened: Event.Happened<*>) {
        if (happened.event in eventFilter)
            eventQueue.add(happened)
    }

    fun <T : Any> on(event: Event<T>, handler: (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        eventFilter.computeIfAbsent(event) { CopyOnWriteArrayList() }.add(handler as (Any) -> Unit)
    }

    private fun clear() {
        finalize()
        temporalRoot.clear()
        visualRoot.clear()
        eventQueue.clear()
        eventFilter.clear()
        timeout = 0
        fps = 0.0
        size = Size.OneOne
        title = ""
    }

    abstract fun setup()

    fun start() = Thread {
        while (running) {
            if (restart) {
                restart = false
                clear()
                setup()
            }
            temporalRoot.progress()
            while (eventQueue.isNotEmpty()) {
                eventQueue.poll()?.let { happened ->
                    eventFilter[happened.event]?.forEach { handle -> handle(happened.context) }
                }
            }
            Thread.sleep(timeout)
        }
        clear()
        process.stop()
    }.start()
}