package org.deg.uni.analysis.functions

import org.deg.uni.analysis.terms.model.*
import org.deg.uni.analysis.terms.parsing.LexiAnalysis
import org.deg.uni.analysis.terms.parsing.SyntacticAnalysis
import org.deg.uni.graphs.datastructures.INode
import org.deg.uni.graphs.datastructures.ITree
import org.deg.uni.graphs.datastructures.Node
import org.deg.uni.graphs.datastructures.Tree
import org.deg.uni.unification.Unifiable

class Equation(var left: Term, var right: Term): Unifiable, INode<Unifiable> {

    override fun element(): Unifiable = this

    override fun isPlaceholder(): Boolean = false

    override fun isCommutative(): Boolean = true

    override fun isAssociative(): Boolean = false

    override fun addNode(node: INode<Unifiable>) = throw NotImplementedError()

    override fun removeNodeAt(i: Int): INode<Unifiable> = throw NotImplementedError()

    override fun getNode(i: Int): INode<Unifiable> {
        return when (i) {
            0 -> left as INode<Unifiable>
            1 -> right as INode<Unifiable>
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun setNode(i: Int, node: INode<Unifiable>) {
        when (i) {
            0 -> left = node.element() as Term
            1 -> right = node.element() as Term
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun nodeSize(): Int = 2

    override fun clone(): INode<Unifiable> = Equation(left.clone(), right.clone())

    fun solve(): List<MutableMap<Variable, Term>> {
        left -= right
        right = Num(0)
        left = left.simplify()
        return separateVariables()
    }

    override fun isUnifiableWith(unifiable: Unifiable): Boolean {
        return unifiable is Equation
    }

    private fun separateVariables(): List<MutableMap<Variable, Term>> {
        when (left) {
            is Power -> return Equation((left as Power).base, Power(right, Num(1, 2))).solve()
            is Sum -> {
                for (el in (left as Sum).indices.reversed()) {
                    try {
                        (left as Sum)[el].toDouble()
                    } catch (e: NotANumberException) {
                        continue
                    }
                    // el is a number
                    right -= (left as Sum)[el]
                    (left as Sum).removeAt(el)
                    left = left.simplify()
                    return separateVariables()
                }
            }
            is Product -> {
                for (el in (left as Product).indices.reversed()) {
                    try {
                        (left as Product)[el].toDouble()
                    } catch (e: NotANumberException) {
                        continue
                    }
                    // el is a number
                    right /= (left as Product)[el]
                    (left as Product).removeAt(el)
                    left = left.simplify()
                    return separateVariables()
                }
            }
            is Num -> {
                if (left == right.simplify()) return listOf(mutableMapOf())
                else throw NoCutException()
            }
            is Variable -> return listOf(mutableMapOf(Pair(left as Variable, right.simplify())))
            else -> throw NotImplementedError("Operation is not implemented for class ${left.javaClass}")
        }
        println(this)
        throw NotImplementedError("Can't solve this equation")
    }

    class NoCutException : Throwable()

    override fun toString(): String {
        return "$left = $right"
    }

    override fun equals(other: Any?): Boolean {
        return other is Equation && (left == other.left && right == other.right || left == other.right && right == other.left)
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }
}

fun String.toEquation(): Equation {
    val tokensAndAssignment = LexiAnalysis().parse(this)
    return SyntacticAnalysis().parseEquation(tokensAndAssignment.first, tokensAndAssignment.second)
}
