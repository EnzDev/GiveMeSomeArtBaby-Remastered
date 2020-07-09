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
        private const val theSeed: Int = 1795683149
        private val mode = MODE.NEW

        var seed = Random.Default.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)

        // private val seeds = listOf(1795683149)
        private val seeds = listOf<Int>()

        fun nextRandom() {
            // Generate an int with the default random
            seed = Random.Default.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
            println(seed)
            theRandom = Random(seed)
        }

        fun nextShift(add: List<Int>?) {
            // Find next value or first
            val work = (if (add != null) (seeds + add) else seeds)
            seed = work.zipWithNext().find { it.first == seed }?.second
                ?: work.first()
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