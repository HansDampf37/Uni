package analysis.terms

import algo.datastructures.Node
import propa.Placeholder
import propa.UnifyingTree
import kotlin.math.pow

class Power(var base: Term, var exponent: Term) : Term {

    override fun times(other: Term) = other * this
    override fun plus(other: Term) = other + this

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

    override fun getComponents(): List<UnifyingTree> = listOf(base, exponent)
    override fun nonCommutativeComponents(): Boolean = true
    override fun isComponent(): Boolean = false
    override fun addComponent(c: UnifyingTree) = throw Placeholder.NoComponents(this)
    override fun removeComponent(c: UnifyingTree) = throw Placeholder.NoComponents(this)
    override fun init(): UnifyingTree = throw IllegalCallerException()

    override fun getNode(i: Int): Node<Term> {
        return when (i) {
            0 -> base
            1 -> exponent
            else -> throw IndexOutOfBoundsException(i)
        }
    }
    override fun setNode(i: Int, node: Node<Term>) {
        when (i) {
            0 -> base = node.get()
            1 -> exponent = node.get()
            else -> throw IndexOutOfBoundsException(i)
        }
    }
    override fun nodeSize(): Int = 2

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
            else -> "$base"
        }
        val expStr = when (exponent) {
            is Sum -> "($exponent)"
            is Product -> "($exponent)"
            else -> "$exponent"
        }
        return "$baseStr^$expStr"
    }

    override fun clone(): Power = Power(base, exponent)
    override fun equals(other: Any?): Boolean {
        if (other is Power) {
            return this.exponent == other.exponent && this.base == other.base
        }
        return false
    }
    override fun hashCode(): Int {
        var result = base.hashCode()
        result = 31 * result + exponent.hashCode()
        return result
    }
}