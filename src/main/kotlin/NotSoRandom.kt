import kotlin.random.Random

/** Created by Enzo Mallard
 *  The 07/07/2020
 */

class NotSoRandom {

    enum class MODE {
        NEW,
        EXISTING,
        THAT,
        SHUFFLE
    }

    companion object {
        private const val theSeed: Int = 1640804512
        private val mode = MODE.SHUFFLE

        var seed = Random.Default.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
        private val seeds = listOf(3954422, 13792079, 10732407, 1457961068)

        fun nextRandom() {
            // Generate an int with the default random
            seed = Random.Default.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
            println(seed)
            theRandom = Random(seed)
        }

        fun nextShift() {
            // Find next value or first
            seed = seeds.zipWithNext().find { it.first == seed }?.second ?: seeds.first()
            println(seed)
            theRandom = Random(seed)
        }

        var theRandom = when (mode) {
            MODE.NEW -> Random(seed)
            MODE.EXISTING -> Random(seeds.random())
            MODE.THAT -> Random(theSeed)
            MODE.SHUFFLE -> {
                seed = seeds.first()
                Random(seed)
            }
        }
    }
}