package org.deg.uni.analysis.terms.model

import org.deg.uni.graphs.datastructures.INode
import org.deg.uni.unification.Unifiable

class Variable(val str: String) : Primitive {
    override fun contains(x: Variable): Boolean = this == x || this.value?.contains(x) ?: false
    override fun derive(x: Variable): Term = if (this == x) Num(1) else Num(0)
    var value
        get() = VariableBindings.getBinding(this)
        set(valu) {
            if (valu == null) VariableBindings.unbind(this)
            else VariableBindings.bind(this, valu)
        }

    override fun getNode(i: Int): INode<Term> {
        return if (value != null) value!!.getNode(i) else throw IndexOutOfBoundsException(i)
    }
    override fun setNode(i: Int, node: INode<Term>) {
        return if (value != null) value!!.setNode(i, node) else throw IndexOutOfBoundsException(i)
    }
    override fun nodeSize(): Int {
        return if (value != null) value!!.nodeSize() else 0
    }
    override fun addNode(node: INode<Term>) = throw NotImplementedError()
    override fun isUnifiableWith(unifiable: Unifiable): Boolean = unifiable is Variable

    override fun toInt(): Int = value?.toInt() ?: throw NotANumberException(this)
    override fun toDouble(): Double = value?.toDouble() ?: throw NotANumberException(this)
    override fun toString(): String = str

    override fun clone(): Variable = this
    override fun equals(other: Any?): Boolean {
        if (other is Variable) return str == other.str
        return false
    }

    override fun hashCode(): Int {
        return str.hashCode()
    }
}

object VariableBindings {
    private val m = HashMap<Variable, Term>()

    fun bind(v: Variable, n: Term) {
        if (m[v] == n || m[v] == null) {
            m[v] = n
        } else {
            throw VariableAlreadyBoundException(v, n, m[v]!!)
        }
    }

    fun unbind(v: Variable) {
        m.remove(v)
    }

    fun getBinding(v: Variable) = m[v]

    class VariableAlreadyBoundException(v: Variable, new: Term, old: Term) : Exception(
        "Variable $v is already bound to" +
                " value $old and can therefore not be bound to value $new"
    )
}