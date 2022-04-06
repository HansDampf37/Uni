package analysis.terms.simplifying

import algo.datastructures.INode
import algo.datastructures.Node
import analysis.terms.*
import propa.Placeholder

class UnificationVariable(name: String, constraint: (INode<Term>) -> Boolean = { true }, t: Term? = null, filler: Boolean = false) :
    Placeholder<Term>(name, constraint, t, filler), Term {

    override fun plus(other: Term): Term = if (subtree != null) (subtree!! as Term).plus(other) else Sum(this, other)
    override fun times(other: Term): Term = if (subtree != null) (subtree!! as Term).times(other) else Product(this, other)

    override fun clone(): Term = UnificationVariable(name, constraint, subtree as Term, filler)
    override fun toString(): String = name
    override fun toDouble(): Double = throw OnlyPlaceholderException()
    override fun toInt(): Int = throw OnlyPlaceholderException()

    override fun getNode(i: Int): INode<Term> {
        return if (subtree != null) subtree!!.getNode(i) else throw IndexOutOfBoundsException(i)
    }
    override fun setNode(i: Int, node: INode<Term>) {
        return if (subtree != null) subtree!!.setNode(i, node) else throw IndexOutOfBoundsException(i)
    }
    override fun nodeSize(): Int {
        return if (subtree != null) subtree!!.nodeSize() else 0
    }
    override fun addNode(node: INode<Term>) = throw NotImplementedError()

    override fun isPlaceholder(): Boolean = true
    override fun isCommutative(): Boolean = false
    override fun isAssociative(): Boolean = false

    override fun contains(x: Variable): Boolean = throw OnlyPlaceholderException()
    override fun derive(x: Variable): Term = throw OnlyPlaceholderException()
    override fun equals(other: Any?): Boolean = other is UnificationVariable && other.name == name && other.subtree == subtree && constraint == other.constraint
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (subtree?.hashCode() ?: 0)
        return result
    }

    class OnlyPlaceholderException : IllegalCallerException("This method is not implemented on this placeholder")
}