package analysis.terms

import algo.datastructures.INode
import analysis.unaryMinus
import propa.Unifiable
import kotlin.math.log

open class Log(var base: Term, var arg: Term) : Term {
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

    override fun getNode(i: Int): INode<Term> {
        return when (i) {
            0 -> base
            1 -> arg
            else -> throw IndexOutOfBoundsException(i)
        }
    }
    override fun setNode(i: Int, node: INode<Term>) {
        when (i) {
            0 -> base = node.element()
            1 -> arg = node.element()
            else -> throw IndexOutOfBoundsException(i)
        }
    }
    override fun nodeSize(): Int = 2

    override fun isUnifiableWith(unifiable: Unifiable): Boolean = unifiable is Log

    override fun toDouble(): Double = log(arg.toDouble(), base.toDouble())
    override fun toInt(): Int = toDouble().toInt()

    override fun clone() = Log(base, arg)
    override fun toString() = if (base is Num) "Log$base($arg)" else "Log_($base)($arg)"
    override fun equals(other: Any?): Boolean = other is Log && other.arg == arg && other.base == base

    override fun hashCode(): Int {
        var result = base.hashCode()
        result = 31 * result + arg.hashCode()
        return result
    }
}

class Ln(arg: Term) : Log(E, arg) {
    override fun toString() = "Ln($arg)"
}
class Log10(arg: Term) : Log(Num(10), arg) {
    override fun toString() = "Log10($arg)"
}
