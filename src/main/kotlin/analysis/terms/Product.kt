package analysis.terms

import analysis.terms.simplifying.ProductSimplifier
import analysis.terms.simplifying.Simplifier
import propa.UnifyingTree

class Product(terms: List<Term>) : ArrayList<Term>(), Term {
    constructor(vararg terms: Term) : this(terms.toList())

    init {
        for (t in terms) {
            add(t)
        }
    }

    override fun times(other: Term) = other * this
    override fun plus(other: Term) = other + this

    override fun contains(x: Variable): Boolean = any { it.contains(x) }
    override fun derive(x: Variable): Term {
        if (!contains(x)) return Num(0)
        val one: Term = Num(1)
        if (size > 2) return fold(one) {acc, term -> Product(acc, term) }.derive(x)
        if (size != 2) throw java.lang.IllegalStateException("Product must contain at least 2 factors")
        return if (this[0].contains(x) && this[1].contains(x)) this[0].derive(x) * this[1] + this[0] * this[1].derive(x)
        else if (this[0].contains(x)) this[0].derive(x) * this[1]
        else if (this[1].contains(x)) this[0] * this[1].derive(x)
        else throw java.lang.IllegalStateException("Case should have been checked in the first line of this method")
    }

    private val comps: MutableList<UnifyingTree> = ArrayList()
    override fun getComponents(): List<UnifyingTree> {
        val cp = comps.toMutableList()
        cp.addAll(this)
        return cp
    }
    override fun componentOrderMatters(): Boolean = false
    override fun isComponent(): Boolean = false
    override fun simplifier(): Simplifier<Product> = ProductSimplifier()
    override fun addComponent(c: UnifyingTree) {
        comps.add(c)
    }
    override fun removeComponent(c: UnifyingTree) {
        comps.remove(c)
    }

    override fun toDouble(): Double = this.fold(1.0) { acc, new -> acc * new.toDouble() }
    override fun toInt(): Int = this.fold(1) { acc, new -> acc * new.toInt() }
    override fun toString() = joinToString(" * ") { t: Term ->
        when (t) {
            is Sum -> "(${t})"
            else -> "$t"
        }
    }

    override fun clone(): Product = Product(this.toList())
    override fun equals(other: Any?): Boolean {
        if (other is Product) {
            return this.all { other.contains(it) } && other.all { this.contains(it) }
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}