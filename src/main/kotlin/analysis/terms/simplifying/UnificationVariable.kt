package analysis.terms.simplifying

import analysis.terms.Product
import analysis.terms.Sum
import analysis.terms.Term
import analysis.terms.Variable
import propa.Placeholder
import propa.UnifyingTree

class UnificationVariable(private val name: String, private var t: Term? = null) : Placeholder, Term {
    override fun name() = name

    override fun getT() = t

    override fun setT(t: UnifyingTree?) {
        this.t = t as Term?
    }

    override fun plus(other: Term): Term = Sum(this, other)
    override fun times(other: Term): Term = Product(this, other)
    override fun clone(): Term = UnificationVariable(name, t)
    override fun toString(): String = "$name"
    override fun toDouble(): Double = throw OnlyPlaceholderException()
    override fun toInt(): Int = throw OnlyPlaceholderException()
    override fun simplifier(): Simplifier<*> = throw OnlyPlaceholderException()
    override fun contains(x: Variable): Boolean = throw OnlyPlaceholderException()
    override fun derive(x: Variable): Term = throw OnlyPlaceholderException()
    override fun equals(other: Any?): Boolean = other is UnificationVariable && other.name == name && other.t == t
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (t?.hashCode() ?: 0)
        return result
    }

    class OnlyPlaceholderException() : IllegalCallerException("This method is not implemented on this placeholder")
}