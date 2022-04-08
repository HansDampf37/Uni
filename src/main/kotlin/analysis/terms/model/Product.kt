package analysis.terms.model

import algo.datastructures.INode
import propa.Unifiable

class Product(terms: List<Term>) : ArrayList<Term>(), Term {
    constructor(vararg terms: Term) : this(terms.toList())

    init {
        for (t in terms) {
            addNode(t)
        }
    }

    override fun contains(x: Variable): Boolean = any { it.contains(x) }
    override fun derive(x: Variable): Term {
        if (!contains(x)) return Num(0)
        val one: Term = Num(1)
        if (size > 2) return fold(one) {acc, term -> Product(acc, term) }.derive(x)
        if (size != 2) throw java.lang.IllegalStateException("Product must contain at least 2 factors")
        return if (this[0].contains(x) && this[1].contains(x)) {
            Sum(Product(this[0].derive(x), this[1]), Product(this[0], this[1].derive(x))).simplify()
        }
        else if (this[0].contains(x)) this[0].derive(x) * this[1]
        else if (this[1].contains(x)) this[0] * this[1].derive(x)
        else throw java.lang.IllegalStateException("Case should have been checked in the first line of this method")
    }

    override fun getNode(i: Int): INode<Term> = this[i]
    override fun setNode(i: Int, node: INode<Term>) {
        this[i] = node.element()
    }
    override fun nodeSize(): Int = size
    override fun addNode(node: INode<Term>){
        this.add(node.element())
    }
    override fun removeNodeAt(i: Int): INode<Term> = removeAt(i)

    override fun isUnifiableWith(unifiable: Unifiable): Boolean = unifiable is Product
    override fun isCommutative(): Boolean = true
    override fun isAssociative(): Boolean = true

    override fun toDouble(): Double = this.fold(1.0) { acc, new -> acc * new.toDouble() }
    override fun toInt(): Int = this.fold(1) { acc, new -> acc * new.toInt() }
    override fun toString() = joinToString(" * ") { t: Term ->
        when (t) {
            is Sum -> "(${t})"
            is Product -> "(${t})"
            else -> "$t"
        }
    }

    override fun clone(): Product = Product(this.toList())
    override fun equals(other: Any?): Boolean = other is Product && other.toSet() == toSet()
    override fun hashCode(): Int {
        return toSet().hashCode()
    }
}