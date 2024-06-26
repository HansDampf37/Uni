package org.deg.uni.analysis.terms.model

import org.deg.uni.analysis.terms.model.Power
import org.deg.uni.analysis.terms.model.Primitive
import org.deg.uni.analysis.terms.model.Term
import org.deg.uni.analysis.terms.model.Variable
import org.deg.uni.analysis.Field
import org.deg.uni.graphs.datastructures.INode
import org.deg.uni.unification.Unifiable
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.withSign

open class Num(var num: Double, var denominator: Double = 1.0) : Primitive, Field<Term> {
    constructor(num: Number, denominator: Number = 1.0) : this(num.toDouble(), denominator.toDouble())

    init {
        if (denominator == 0.0) throw ArithmeticException("Division by 0")
        if (num.toInt().toDouble() != num) {
            val shift = num.digitsAfterComma()
            if (shift < 5) {
                num *= 10.0.pow(shift)
                denominator = 10.0.pow(shift)
            }
        }
        shorten()
    }

    override fun zero() = Num(0)
    override fun one() = Num(1)

    fun plus(other: Num): Num {
        return if (denominator != other.denominator) {
            Num(this.num * other.denominator + other.num * this.denominator, other.denominator * this.denominator)
        } else {
            Num(this.num + other.num, denominator)
        }
    }
    fun times(other: Num): Num {
        val (n,d) = if (num % other.denominator == 0.0) {
            // 6/5 * 3/2
            Pair(num / other.denominator * other.num, denominator)
        } else if (other.num % denominator == 0.0) {
            Pair(other.num / denominator * num, other.denominator)
        } else if (denominator % other.num == 0.0) {
            // 5/6 * 2/3
            Pair(num, denominator / other.num * other.denominator)
        } else if (other.denominator % num == 0.0) {
            Pair(other.num, other.denominator / num * denominator)
        } else {
            Pair(this.num * other.num, this.denominator * other.denominator)
        }
        return Num(n, d)
    }

    override fun inverseMult(): Term = Num(denominator, num)
    override fun inverseAdd(): Term = Num(-num, denominator)

    override fun pow(t: Term): Term {
        if (t !is Num) return super.pow(t)
        val numerator = num.pow(t.toDouble())
        val denominator = denominator.pow(t.toDouble())
        val first = if (numerator.toString().contains(".")) numerator.toString().split(".")[1].length else 1
        val second = if (denominator.toString().contains(".")) denominator.toString().split(".")[1].length else 1
        val lengths = Pair(first, second)
        return if (lengths.first < 5 && lengths.second < 5) {
            Num(numerator, denominator)
        } else {
            Power(this, t)
        }
    }
    override fun log(arg: Term): Term {
        if (arg !is Num) return super.log(arg)
        val result = log(arg.toDouble(), toDouble())
        val split = result.toString().split(".")
        return if (split.size == 1 || split[1].length < 5) {
            Num(result)
        } else {
            Log(this, arg)
        }
    }

    override fun contains(x: Variable): Boolean = false
    override fun derive(x: Variable): Term = Num(0)

    override fun getNode(i: Int): INode<Term> {
        throw IndexOutOfBoundsException(i)
    }
    override fun setNode(i: Int, node: INode<Term>) {
        throw IndexOutOfBoundsException(i)
    }
    override fun addNode(node: INode<Term>) = throw NotImplementedError()
    override fun nodeSize(): Int = 0
    override fun equals(other: Any?): Boolean {
        if (other is Number) return toDouble() == other.toDouble()
        else if (other is Num) return toDouble() == other.toDouble()
        return false
    }

    override fun clone(): Num = Num(num, denominator)
    override fun hashCode(): Int {
        var result = num.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
    override fun toString(): String {
        return if (denominator.withSign(1) == 1.0) {
            (num * denominator.sign).toStringShort()
        } else {
            "${num.toStringShort()} / ${denominator.toStringShort()}"
        }
    }

    override fun toDouble(): Double {
        return  num / denominator
    }
    override fun toInt(): Int {
        return  (num / denominator).toInt()
    }

    override fun isUnifiableWith(unifiable: Unifiable): Boolean = unifiable is Num

    private fun shorten(): Num {
        if (num % denominator == 0.0) {
            // 10/5
            num /= denominator
            denominator = 1.0
            return this
        } else if (denominator % num == 0.0) {
            // 3/9
            denominator /= num
            num = 1.0
            return this
        }
        for (divisor in (2 .. minOf(num, denominator).withSign(1).toInt() / 2)) {
            if (num % divisor == 0.0 && denominator % divisor == 0.0) {
                denominator /= divisor
                num /= divisor
                shorten()
                return this
            }
        }
        return this
    }
}

fun Double.toStringShort(): String {
    val str = if (this.toInt().toDouble() == this) {
        // is Int
        this.toInt().toString()
    } else {
        this.toString()
    }
    return str
}

fun Double.digitsAfterComma(): Int {
    for (i in 0 until 20) {
        if (((this * 10.0.pow(i)) % 10.0) == 0.0) {
            return i
        }
    }
    return 100
}

fun String.toNum(): Num {
    if (contains("/")) {
        val parts = split("/")
        val num = parts[0].toDouble()
        val denom = parts[1].toDouble()
        return Num(num, denom)
    }
    return Num(toDouble())
}