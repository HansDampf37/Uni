package analysis.terms

import algo.datastructures.INode
import propa.UnifyingTree

class Sum(terms: List<Term>) : ArrayList<Term>(), Term {

    constructor(vararg terms: Term) : this(terms.toList())

    init {
        for (t in terms) {
            add(t)
        }
    }

    override fun contains(x: Variable): Boolean = any { it.contains(x) }
    override fun derive(x: Variable): Term = Sum(map{ it.derive(x) }).simplify()

    override fun getComponents(): List<UnifyingTree> = this.toList()
    override fun nonCommutativeComponents(): Boolean = false
    override fun isComponent(): Boolean = false
    override fun addComponent(c: UnifyingTree) {
        this.add(c as Term)
    }
    override fun removeComponent(c: UnifyingTree) {
        this.remove(c as Term)
    }
    override fun init(): UnifyingTree = Sum()

    override fun getNode(i: Int): INode<Term> = this[i]
    override fun setNode(i: Int, node: INode<Term>) {
        this[i] = node.get()
    }
    override fun nodeSize(): Int = size

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