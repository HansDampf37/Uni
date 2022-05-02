package org.deg.uni.analysis.terms.model

import org.deg.uni.graphs.datastructures.INode
import org.deg.uni.unification.Unifiable
import kotlin.math.pow

class Power(var base: Term, var exponent: Term) : Term {

    override fun contains(x: Variable): Boolean = base.contains(x) || exponent.contains(x)
    override fun derive(x: Variable): Term {
        return if (base.contains(x) && exponent.contains(x)) {
            (exponent.derive(x) * Ln(base) + exponent / base) * this.clone()
        } else if (base.contains(x)) {
            exponent * Power(base, exponent - Num(1)) * base.derive(x)
        } else if (exponent.contains(x)) {
            Ln(base) * this.clone() * exponent.derive(x)
        } else {
            Num(0)
        }
    }

    override fun getNode(i: Int): INode<Term> {
        return when (i) {
            0 -> base
            1 -> exponent
            else -> throw IndexOutOfBoundsException(i)
        }
    }
    override fun setNode(i: Int, node: INode<Term>) {
        when (i) {
            0 -> base = node.element()
            1 -> exponent = node.element()
            else -> throw IndexOutOfBoundsException(i)
        }
    }
    override fun nodeSize(): Int = 2
    override fun addNode(node: INode<Term>) = throw NotImplementedError()

    override fun isUnifiableWith(unifiable: Unifiable): Boolean = unifiable is Power

    override fun toDouble(): Double {
        return base.simplify().toDouble().pow(exponent.simplify().toDouble())
    }
    override fun toInt(): Int {
        return base.simplify().toDouble().pow(exponent.simplify().toDouble()).toInt()
    }
    override fun toString(): String {
        val baseStr = when (base) {
            is Sum -> "($base)"
            is Product -> "($base)"
            is Power -> "($base)"
            is Num -> {
                val str = base.toString()
                if (str.contains("/")) "($str)"
                else str
            }
            else -> "$base"
        }
        val expStr = when (exponent) {
            is Sum -> "($exponent)"
            is Product -> "($exponent)"
            is Num -> {
                val str = exponent.toString()
                if (str.contains("/")) "($str)"
                else str
            }
            else -> "$exponent"
        }
        return "$baseStr^$expStr"
    }

    override fun clone(): Power = Power(base, exponent)
    override fun equals(other: Any?): Boolean = other is Power && other.exponent == exponent && other.base == base
    override fun hashCode(): Int {
        var result = base.hashCode()
        result = 31 * result + exponent.hashCode()
        return result
    }
}