package ir.smmh.mage.pacman

import ir.smmh.mage.core.BasicApp
import ir.smmh.mage.core.Platform
import kotlin.math.round

class PacmanGame(platform: Platform, private val seed: String = defaultSeed) : BasicApp(platform) {

    override fun main() {
        val world: World = World.fromText(seed)
        val padding = size.width * 0.05
        world.offset.x = padding
        world.offset.y = padding
        world.scale = round((size.width - padding * 2) / world.size.width)
        val agent = Agent(world)
        add(world)
        add(agent)
    }

    companion object {
        private const val defaultSeed =
            "*************************\n*  x  *   *            x*\n* *** * * * ******* *** *\n*   * *x*   *     *** * *\n* * * ***** * * *       *\n* * *   *x* * * ***** ***\n* * * *   * * *         *\n* * * ***** * **** **** *\n* *                  x*x*\n* *********** ********* *\n*  x* *   *   *         *\n***** * * * *** *********\n*x      *   *          x*\n*************************"
    }
}