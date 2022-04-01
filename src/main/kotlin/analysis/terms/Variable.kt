package analysis.terms

import algo.datastructures.INode
import propa.Placeholder
import propa.UnifyingTree

class Variable(val str: String) : Primitive, TermContainer {
    override var value: Term?
        get() = VariableBindings.getBinding(this)
        set(v) {
            if (v == null) VariableBindings.unbind(this)
            else VariableBindings.bind(this, v)
        }

    override fun contains(x: Variable): Boolean = this == x || this.value?.contains(x) ?: false
    override fun derive(x: Variable): Term = if (this == x) Num(1) else Num(0)

    override fun getComponents(): List<UnifyingTree> = value?.getComponents() ?: throw Placeholder.NoComponents(this)
    override fun nonCommutativeComponents(): Boolean =
        value?.nonCommutativeComponents() ?: throw Placeholder.NoComponents(this)

    override fun isComponent(): Boolean = value?.isComponent() ?: throw Placeholder.NoComponents(this)
    override fun addComponent(c: UnifyingTree) = throw Placeholder.NoComponents(this)
    override fun removeComponent(c: UnifyingTree) = throw Placeholder.NoComponents(this)
    override fun init(): UnifyingTree = throw IllegalCallerException()

    override fun getNode(i: Int): INode<Term> {
        return if (value != null) value!!.getNode(i) else throw IndexOutOfBoundsException(i)
    }
    override fun setNode(i: Int, node: INode<Term>) {
        return if (value != null) value!!.setNode(i, node) else throw IndexOutOfBoundsException(i)
    }
    override fun nodeSize(): Int {
        return if (value != null) value!!.nodeSize() else 0
    }

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