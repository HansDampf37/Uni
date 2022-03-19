package analysis.terms

import algo.datastructures.Node
import propa.UnifyingTree

class Sum(terms: List<Term>) : ArrayList<Term>(), Term {

    constructor(vararg terms: Term) : this(terms.toList())

    init {
        for (t in terms) {
            add(t)
        }
    }

    override fun plus(other: Term): Term = other + this
    override fun times(other: Term): Term = other * this

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

    override fun getNode(i: Int): Node<Term> = this[i]
    override fun setNode(i: Int, node: Node<Term>) {
        this[i] = node.get()
    }
    override fun nodeSize(): Int = size

    override fun toInt(): Int = sumOf { it.toInt() }
    override fun toDouble(): Double = sumOf { it.toDouble() }
    override fun toString() = joinToString(" + ") { t: Term -> t.toString() }

    override fun clone(): Sum = Sum(this.toList())
    override fun equals(other: Any?): Boolean {
        if (other is Sum) {
            return this.all { other.contains(it) } && other.all { this.contains(it) }
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}