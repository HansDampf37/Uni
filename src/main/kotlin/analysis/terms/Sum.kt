package analysis.terms

import algo.datastructures.INode
import propa.Unifiable

class Sum(terms: List<Term>) : ArrayList<Term>(), Term {

    constructor(vararg terms: Term) : this(terms.toList())

    init {
        for (t in terms) {
            addNode(t)
        }
    }

    override fun contains(x: Variable): Boolean = any { it.contains(x) }
    override fun derive(x: Variable): Term = Sum(map{ it.derive(x) }).simplify()

    override fun getNode(i: Int): INode<Term> = this[i]
    override fun setNode(i: Int, node: INode<Term>) {
        this[i] = node.element()
    }
    override fun nodeSize(): Int = size
    override fun addNode(node: INode<Term>){
        this.add(node.element())
    }
    override fun removeNodeAt(i: Int): INode<Term> = removeAt(i)

    override fun isUnifiableWith(unifiable: Unifiable): Boolean = unifiable is Sum
    override fun isCommutative(): Boolean = true
    override fun isAssociative(): Boolean = true

    override fun toInt(): Int = sumOf { it.toInt() }
    override fun toDouble(): Double = sumOf { it.toDouble() }
    override fun toString() = joinToString(" + ") { t: Term ->
        when (t) {
            is Sum -> "(${t})"
            else -> "$t"
        }
    }

    override fun clone(): Sum = Sum(this.toList())
    override fun equals(other: Any?): Boolean = other is Sum && other.toSet() == toSet()
    override fun hashCode(): Int {
        return toSet().hashCode()
    }
}