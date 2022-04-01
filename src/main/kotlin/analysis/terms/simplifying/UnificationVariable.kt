package analysis.terms.simplifying

import algo.datastructures.INode
import analysis.terms.*
import propa.Placeholder
import propa.UnifyingTree

class UnificationVariable(name: String, constraint: (UnifyingTree) -> Boolean = { true }, t: Term? = null, filler: Boolean = false) :
    Placeholder(name, constraint, t, false, filler), Term, TermContainer {
    override var value: Term?
        get() = t as Term?
        set(value) {
            t = value
        }

    override fun plus(other: Term): Term = if (t != null) (t!! as Term).plus(other) else Sum(this, other)
    override fun times(other: Term): Term = if (t != null) (t!! as Term).times(other) else Product(this, other)

    override fun clone(): Term = UnificationVariable(name, constraint, value, filler)
    override fun toString(): String = name
    override fun toDouble(): Double = throw OnlyPlaceholderException()
    override fun toInt(): Int = throw OnlyPlaceholderException()

    override fun getNode(i: Int): INode<Term> {
        return if (value != null) value!!.getNode(i) else throw IndexOutOfBoundsException(i)
    }
    override fun setNode(i: Int, node: INode<Term>) {
        return if (value != null) value!!.setNode(i, node) else throw IndexOutOfBoundsException(i)
    }
    override fun nodeSize(): Int {
        return if (value != null) value!!.nodeSize() else 0
    }

    override fun contains(x: Variable): Boolean = throw OnlyPlaceholderException()
    override fun derive(x: Variable): Term = throw OnlyPlaceholderException()
    override fun equals(other: Any?): Boolean = other is UnificationVariable && other.name == name && other.t == t && constraint == other.constraint
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (t?.hashCode() ?: 0)
        return result
    }

    class OnlyPlaceholderException : IllegalCallerException("This method is not implemented on this placeholder")
}