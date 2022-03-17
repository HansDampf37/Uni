package analysis.terms

import analysis.inverseMult
import analysis.terms.simplifying.LogSimplifier
import analysis.terms.simplifying.Simplifier
import analysis.unaryMinus
import propa.UnifyingTree
import kotlin.math.log

open class Log(var base: Term, var arg: Term) : Term {
    override fun plus(other: Term): Term = other + this
    override fun times(other: Term): Term = other * this
    override fun plus(l: Log): Term {
        return if (l.base == this.base) Log(base, arg * l.arg) else Sum(this, l)
    }
    override fun minus(l: Log): Term = if (l.base == this.base) Log(base, arg / l.arg) else Sum(this, -l)
    override fun div(l: Log): Term = if (l.base == this.base) Log(l.arg, this.arg) else Product(this, l.inverseMult())

    override fun contains(x: Variable): Boolean = base.contains(x) || arg.contains(x)
    override fun derive(x: Variable): Term {
        if (!contains(x)) return Num(0)
        if (base.contains(x) && arg.contains(x)) {
            return ((Ln(base) * arg.derive(x) / arg) - (base.derive(x) * Ln(arg) / base)) / Ln(base).pow(2)
        } else if (base.contains(x) && !arg.contains(x)) {
            return -Ln(arg) * base.derive(x) / (base * Ln(base).pow(2))
        }
        return arg.derive(x) / (arg * Ln(base))
    }

    override fun getComponents(): List<UnifyingTree> {
        return listOf(base, arg)
    }
    override fun componentOrderMatters(): Boolean = true
    override fun isComponent(): Boolean = false

    override fun simplifier(): Simplifier<*> = LogSimplifier()

    override fun toDouble(): Double = log(arg.toDouble(), base.toDouble())
    override fun toInt(): Int = toDouble().toInt()

    override fun clone() = Log(base, arg)
    override fun toString() = if (base is Num) "Log$base($arg)" else "Log_($base)($arg)"
    override fun equals(other: Any?): Boolean {
        return if (other is Log) other.base == base && other.arg == arg
        else false
    }

    override fun hashCode(): Int {
        var result = base.hashCode()
        result = 31 * result + arg.hashCode()
        return result
    }
}

class Ln(arg: Term) : Log(Num(2), arg) {
    override fun toString() = "Ln($arg)"
}
class Log10(arg: Term) : Log(Num(10), arg) {
    override fun toString() = "Log10($arg)"
}
