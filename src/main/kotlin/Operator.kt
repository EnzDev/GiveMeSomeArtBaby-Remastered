import NotSoRandom.Companion.theRandom
import kotlin.math.sin

/** Created by Enzo Mallard
 *  The 07/07/2020
 */

data class Color(val r: Double, val g: Double, val b: Double) {
    operator fun plus(e: Color): Color = Color(this.r + e.r, this.g + e.g, this.b + e.b)

    operator fun div(i: Int) = Color(r * i, g * i, b * i)
    fun sin(phase: Double, freq: Double) =
        Color(
            sin(phase + freq * this.r),
            sin(phase + freq * this.g),
            sin(phase + freq * this.b)
        )

    companion object {
        fun random(): Color = Color(theRandom.nextDouble(), theRandom.nextDouble(), theRandom.nextDouble())
        fun level(threshold: Double, cLevel: Color, c0: Color, c1: Color): Color {
            return Color(
                if (cLevel.r < threshold) c0.r else c1.r,
                if (cLevel.g < threshold) c0.g else c1.g,
                if (cLevel.b < threshold) c0.b else c1.b
            )
        }
    }
}

abstract class Operator(val arity: Int) {
    abstract fun eval(x: Double, y: Double, t: Double): Color
}

class VariableX : Operator(0) {
    override fun eval(x: Double, y: Double, t: Double): Color {
        return Color(x, x, x)
    }

    override fun toString(): String = "x"
}

class VariableY : Operator(0) {
    override fun eval(x: Double, y: Double, t: Double): Color {
        return Color(y, y, y)
    }

    override fun toString(): String = "y"
}

class VariableT : Operator(0) {
    override fun eval(x: Double, y: Double, t: Double): Color {
        return Color(t, t, t)
    }

    override fun toString(): String = "t"
}

class Sum(private val e1: Operator, private val e2: Operator) : Operator(2) {
    override fun eval(x: Double, y: Double, t: Double): Color = (e1.eval(x, y, t) + e2.eval(x, y, t)) / 2

    override fun toString(): String = "$e1 + $e2"
}

class Constant : Operator(0) {
    private val c: Color = Color.random()
    override fun eval(x: Double, y: Double, t: Double): Color = this.c
    override fun toString(): String = "($c, $c, $c)"
}

class Sin(private val e: Operator) : Operator(1) {
    private val phase = theRandom.nextDouble(0.0, Math.PI)
    private val freq = theRandom.nextDouble(1.0, 6.0)

    override fun eval(x: Double, y: Double, t: Double): Color = e.eval(x, y, t).sin(phase, freq)

    override fun toString(): String = "sin($phase + $freq * ($e))"
}

class Level(private val level: Operator, private val e1: Operator, private val e2: Operator) : Operator(3) {
    private val threshold = theRandom.nextDouble(-1.0, 1.0)

    override fun eval(x: Double, y: Double, t: Double): Color =
        Color.level(threshold, level.eval(x, y, t), e1.eval(x, y, t), e2.eval(x, y, t))

    override fun toString(): String = "Level($level < $threshold)($e1,$e2)"
}
