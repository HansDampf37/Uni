package analysis.terms

import kotlin.math.pow

class Power(var base: Term, var exponent: Term) : Term {

    override fun times(other: Term) = other * this
    override fun plus(other: Term) = other + this

    override fun contains(x: Variable): Boolean = base.contains(x) || exponent.contains(x)
    override fun derive(x: Variable): Term {
        return if (base.contains(x) && exponent.contains(x)) {
            (exponent.derive(x) * Variable("ln($base)") + exponent / base) * this.clone()
        } else if (base.contains(x)) {
            exponent * Power(base, exponent - Num(1)) * base.derive(x)
        } else if (exponent.contains(x)) {
            Variable("ln($base)") * this.clone() * exponent.derive(x)
        } else {
            Num(0)
        }
    }

    override fun simplifier(): Simplifier<Power> = PowerSimplifier()

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