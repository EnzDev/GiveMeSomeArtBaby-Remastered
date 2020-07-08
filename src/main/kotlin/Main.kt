import NotSoRandom.Companion.theRandom
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorType
import org.openrndr.draw.colorBuffer
import org.openrndr.ffmpeg.VideoWriter
import kotlin.math.PI
import kotlin.math.sin
import kotlin.reflect.KFunction

private val KFunction<Operator>.arity: Int
    get() = this.parameters.size

/** Created by Enzo Mallard
 *  The 07/07/2020
 */
val operators =
    listOf(VariableX::class, VariableY::class, VariableT::class, Sum::class, Sin::class, Constant::class, Level::class)


val operators0 = operators.filter { it.constructors.first().parameters.isEmpty() }
val operators1 = operators.filter { it.constructors.first().parameters.isNotEmpty() }


fun main() = application {
    configure {
        println(NotSoRandom.seed)
        width = 1024
        height = 1024
    }

    program {
        var function = generate(20)
        var nextFunction: Operator? = null
        var t = 0.0

        var step = 0.01

        val cb = colorBuffer(width, height, type = ColorType.UINT8)
        val shadow = cb.shadow

        /** Recording attributes **/
        val videoWriter: VideoWriter = VideoWriter.create()
            .size(width, height)
        var recording = 2
        var nFrame = 0
        var tStart: Double = t
        var record = false

        keyboard.keyUp.listen {
            when (it.key) {
                265 -> {
                    /** up */
                    step *= 2
                }
                264 -> {
                    /** down */
                    step /= 2
                }
            }
        }

        keyboard.character.listen {
            if (it.character == 'n') {
                NotSoRandom.nextShift()
                nextFunction = generate(20)
            }

            if (it.character == 'h') {
                NotSoRandom.nextRandom()
                nextFunction = generate(20)
            }

            if (it.character == 'r') {
                // start recording
                videoWriter.output("output_recording_n${recording.format("03")}.mp4".also { t -> println("Start recording ($t)...") })
                    .start()
                recording++
                tStart = t
                record = true
                nFrame = 0
            }
        }

        extend {
            shadow.download()

            (0 until width).toList().parallelStream().forEach { w ->
                (0 until height).toList().parallelStream().forEach { h ->
                    val u = 2 * (w + 0.5) / width - 1.0
                    val v = 2 * (h + 0.5) / height - 1.0
                    val color = function.eval(u, v, sin(t))

                    shadow[w, h] = ColorRGBa(color.r, color.g, color.b, a = 1.0)
                }
            }

            shadow.upload()
            drawer.image(cb)
            if (record) {
                videoWriter.frame(cb)
                println("Frame $nFrame")
            }

            if (record) {
                nFrame++
                if (tStart + 1.5 * PI < t) { // last section
                    // look at the direction
                    if ((sin(t) > sin(t + step) && sin(t + step) < sin(tStart))
                        || (sin(t) < sin(t + step) && sin(t + step) > sin(tStart))
                    ) {
                        println("End recording...")
                        videoWriter.stop()
                        record = false
                    }
                }
            }

            t += step

            nextFunction?.let {
                step = 0.01
                t = 0.0
                function = it
                nextFunction = null

                if (record) {
                    println("End recording...")
                    videoWriter.stop()
                }

                record = false
            }


        }
    }
}

fun generate(k: Int = 50): Operator {
    if (k <= 0) // No more ops, put a leaf
        return operators0.random(theRandom).constructors.first().call()

    // Generate a node with an non leaf operator
    val op = operators1.random(theRandom).constructors.first()

    var i = 0
    val args = mutableListOf<Operator>()

    // Fill almost all the arguments for the operator and leave them a part of the left operators (k)
    IntRange(1, op.arity - 1).map { theRandom.nextInt(k + 1) }.sorted().forEach {
        args.add(generate(it - i)) // Generate a subtree of operators with their attributed operators
        i = it // Decrease the max counter
    }
    // fill the last arguments with the operators left
    args.add(generate(k - 1 - i))

    // Generate the operators with the generated arguments
    return op.call(*args.toTypedArray())
}

fun Int.format(digits: String): String {
    return "%${digits}d".format(this)
}
