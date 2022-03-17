package analysis.terms.simplifying

import analysis.terms.*
import propa.Placeholder
import propa.UnifyingTree

class UnificationVariable(private val name: String, private var t: Term? = null) : Placeholder, Term, TermContainer(){
    override fun name() = name

    override fun getT() = t

    override fun setT(t: UnifyingTree?) {
        this.t = t as Term?
    }

    override var value: Term?
        get() = getT()
        set(value) = setT(value)

    override fun plus(other: Term): Term = Sum(this, other)
    override fun times(other: Term): Term = Product(this, other)
    override fun clone(): Term = UnificationVariable(name, t)
    override fun toString(): String = name
    override fun toDouble(): Double = throw OnlyPlaceholderException()
    override fun toInt(): Int = throw OnlyPlaceholderException()
    override fun simplifier(): Simplifier<*> = object : Simplifier<UnificationVariable> {
        override fun simplify(t: UnificationVariable): Term = if(t.getT() != null) t.getT()!!.simplify() else t
        override fun flatten(t: UnificationVariable): Term = if(t.getT() != null) t.getT()!!.flatten() else t
        override fun eval(t: UnificationVariable): Term = if(t.getT() != null) t.getT()!!.eval() else t
        override fun pullUp(t: UnificationVariable): Term {
            return if (value == null) this@UnificationVariable else value!!
        }
    }
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