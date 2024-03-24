package org.deg.uni.analysis.terms.model

import org.deg.uni.analysis.Field
import org.deg.uni.analysis.terms.parsing.LexiAnalysis
import org.deg.uni.analysis.terms.parsing.SyntacticAnalysis
import org.deg.uni.analysis.terms.simplifying.SimplifierGraph
import org.deg.uni.graphs.datastructures.INode
import org.deg.uni.graphs.datastructures.ITree
import org.deg.uni.graphs.datastructures.Tree
import org.deg.uni.unification.Unifiable

/**
 * A mathematical expression. It is modeled as a [Tree] with contained terms as [nodes][INode]
 */
interface Term : Cloneable, Field<Term>, Comparable<Term>, Unifiable, INode<Term> {

    override fun element(): Term = this
    override fun toTree(): ITree<Term> = Tree(this)

    override fun isPlaceholder(): Boolean = false
    override fun isCommutative(): Boolean = false
    override fun isAssociative(): Boolean = false
    override fun addNode(node: INode<Term>): Unit = throw NotImplementedError()
    override fun removeNodeAt(i: Int): INode<Term> = throw NotImplementedError()

    override operator fun plus(other: Term): Term = Sum(this, other).simplify()
    override operator fun times(other: Term): Term = Product(this, other).simplify()
    override operator fun div(other: Term): Term = times(other.inverseMult())
    override operator fun minus(other: Term): Term = plus(other.inverseAdd())

    override fun inverseMult(): Term = Power(this, Num(-1))
    override fun inverseAdd(): Term = Product(this, Num(-1))

    fun sqrt(): Term = Power(this, Num(1, 2)).simplify()
    fun log(arg: Number) = Log(this, Num(arg)).simplify()
    fun log(arg: Term) = Log(this, arg).simplify()
    fun pow(num: Number): Term = Power(this, Num(num.toDouble())).simplify()
    fun pow(t: Term): Term = Power(this, t).simplify()

    override fun clone(): Term

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
        } catch (_: NotANumberException) {
            try {
                simplify2 = (this / other).simplify()
                val compare = simplify2.toDouble()
                if (compare > 1) 1 else if (compare == 1.0) 0 else -1
            } catch (_: NotANumberException) {
                throw NotComparableException(this, other, simplify1, simplify2)
            }
        }
    }
    operator fun compareTo(other: Number): Int = compareTo(Num(other.toFloat()))

    fun contains(x: Variable): Boolean
    fun derive(x: Variable): Term
}

class NotANumberException(t: Term) : Exception("$t can not be simplified to a number")
class NotComparableException(t1: Term, t2: Term, t1MinT2Simp: Term, t1DivT2Simp: Term) : Exception(
    "$t1 can not be compared to $t2 since " +
            "$t1 - $t2 = $t1MinT2Simp and $t1 / $t2 = $t1DivT2Simp which are no Numbers"
)

interface Primitive : Term

@Suppress("UNCHECKED_CAST")
fun <T : Term> T.simplify(showUI : Boolean = false) = SimplifierGraph(showUI).simplify(this)

fun String.toTerm(): Term {
    val tokensAndAssignment = LexiAnalysis().parse(this)
    return SyntacticAnalysis().parseTerm(tokensAndAssignment.first, tokensAndAssignment.second)
}
