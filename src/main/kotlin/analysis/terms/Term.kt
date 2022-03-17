package analysis.terms

import analysis.*
import analysis.terms.simplifying.Simplifier
import propa.UnifyingTree

interface Term : Cloneable, Field<Term>, Comparable<Term>, UnifyingTree {

    operator fun times(sum: Sum): Term = Product(this, sum).simplify()
    operator fun times(prod: Product): Term = Product(this).apply { addAll(prod) }.simplify()
    operator fun times(pow: Power): Term = Product(this, pow).simplify()
    operator fun times(v: Variable): Term = Product(this, v).simplify()
    operator fun times(l: Log): Term = Product(this, l).simplify()
    operator fun times(other: Num): Term = Product(this, other).simplify()

    operator fun plus(sum: Sum): Term = Sum(this).apply { addAll(sum) }.simplify()
    operator fun plus(prod: Product): Term = Sum(this, prod).simplify()
    operator fun plus(pow: Power): Term = Sum(this, pow).simplify()
    operator fun plus(v: Variable): Term = Sum(this, v).simplify()
    operator fun plus(l: Log): Term = Sum(this, l).simplify()
    operator fun plus(other: Num): Term = Sum(this, other).simplify()

    operator fun div(sum: Sum): Term = this * sum.flatten().inverseMult().simplify()
    operator fun div(prod: Product): Term = this * prod.flatten().inverseMult().simplify()
    operator fun div(pow: Power): Term = this * pow.flatten().inverseMult().simplify()
    operator fun div(v: Variable): Term = this * v.inverseMult().simplify()
    operator fun div(l: Log): Term = this * l.inverseMult().simplify()
    operator fun div(other: Num): Term = this * other.inverseMult().simplify()

    operator fun minus(sum: Sum): Term = this + sum.flatten().inverseAdd().simplify()
    operator fun minus(prod: Product): Term = this + prod.flatten().inverseAdd().simplify()
    operator fun minus(pow: Power): Term = this + pow.flatten().inverseAdd().simplify()
    operator fun minus(v: Variable): Term = this + v.inverseAdd().simplify()
    operator fun minus(l: Log): Term = this + l.inverseAdd().simplify()
    operator fun minus(other: Num): Term = this + other.inverseAdd().simplify()

    override fun inverseMult(e: Term): Term {
        return when (e) {
            is Power -> Power(e.base, -e.exponent)
            is Num -> Num(e.denominator, e.num)
            is Product -> Product(e.map { it.inverseMult() })
            else -> Power(e, Num(-1))
        }
    }
    override fun inverseAdd(e: Term): Term {
        return when (e) {
            is Num -> Num(-e.num, e.denominator)
            else -> Product(e, Num(-1))
        }
    }

    fun sqrt(): Term {
        return when (this) {
            is Num -> Power(this, Num(1, 2)).simplify()
            is Power -> Power(this.base, this.exponent / Num (2))
            else -> Power(this, Num(1, 2))
        }
    }
    fun pow(num: Number): Term = Power(this, Num(num.toDouble())).simplify()
    fun pow(t: Term): Term = Power(this, t).simplify()

    public override fun clone(): Term

    /**
     * Returns a double if this expression can be simplified to a number, else throw NotANumberException
     *
     * @return simplifies this expression into a double
     * @throws NotANumberException
     */
    fun toDouble(): Double

    /**
     * @see toDouble
     */
    fun toInt(): Int

    /**
     * Simplifier returns a simplifier<this.class>
     *
     * @return
     */
    fun simplifier(): Simplifier<*>

    override fun zero() = Num(0)
    override fun one() = Num(1)

    override fun compareTo(other: Term): Int {
        var simplify1: Term = Num(1)
        var simplify2: Term = Num(1)
        return try {
            // TODO create lower bounds for terms like x² + 4 >= 4
            simplify1 = (this - other).simplify()
            val compare = simplify1.toDouble()
            if (compare > 0) 1 else if (compare == 0.0) 0 else -1
        } catch (_ : NotANumberException) {
            try {
                simplify2 = (this / other).simplify()
                val compare = simplify2.toDouble()
                if (compare > 1) 1 else if (compare == 1.0) 0 else -1
            } catch (_ : NotANumberException) {
                throw NotComparableException(this, other, simplify1, simplify2)
            }
        }
    }

    operator fun compareTo(other: Number): Int = compareTo(Num(other.toFloat()))

    fun contains(x: Variable): Boolean
    fun derive(x: Variable): Term
}

class NotANumberException(t: Term) : Exception("$t can not be simplified to a number")
class NotComparableException(t1: Term, t2: Term, t1MinT2Simp: Term, t1DivT2Simp: Term)
    : Exception("$t1 can not be compared to $t2 since " +
        "$t1 - $t2 = $t1MinT2Simp and $t1 / $t2 = $t1DivT2Simp which are no Numbers")

interface Primitive : Term

@Suppress("UNCHECKED_CAST")
fun <T : Term> T.simplify() = (this.simplifier() as Simplifier<T>).simplify(this)

@Suppress("UNCHECKED_CAST")
fun <T : Term> T.flatten() = (this.simplifier() as Simplifier<T>).flatten(this)

@Suppress("UNCHECKED_CAST")
fun <T : Term> T.eval() = (this.simplifier() as Simplifier<T>).eval(this)

@Suppress("UNCHECKED_CAST")
fun <T : Term> T.pullUp() = (this.simplifier() as Simplifier<T>).pullUp(this)
