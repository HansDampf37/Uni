package analysis.terms.simplifying

import analysis.terms.*
import propa.Placeholder
import propa.UnifyingTree

class UnificationVariable(name: String, constraint: (UnifyingTree) -> Boolean = { true }, t: Term? = null) :
    Placeholder(name, constraint, t, false), Term, TermContainer {
    override var value: Term?
        get() = t as Term?
        set(value) {
            t = value
        }

    override fun plus(other: Term): Term = Sum(this, other)
    override fun times(other: Term): Term = Product(this, other)
    override fun clone(): Term = UnificationVariable(name, constraint, value)
    override fun toString(): String = name
    override fun toDouble(): Double = throw OnlyPlaceholderException()
    override fun toInt(): Int = throw OnlyPlaceholderException()
    override fun simplifier(): Simplifier<*> = object : Simplifier<UnificationVariable>(listOf()) {
        override fun simplify(t: UnificationVariable): Term = if (t.t != null) (t.t!! as Term).simplify() else t
        override fun eval(t: UnificationVariable): Term = if (t.t != null) (t.t!! as Term).eval() else t
        override fun pullUp(t: UnificationVariable): Term {
            return if (value == null) this@UnificationVariable else value!!
        }
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