package analysis.terms.simplifying

import algo.datastructures.DFS
import algo.datastructures.INode
import propa.Placeholder
import propa.Unifiable
import propa.Unifier

class UnificationRule<T: Unifiable>(val unificator: INode<T>, val result: () -> INode<T>) : IRule<INode<T>, INode<T>> {
    override fun applicable(x: INode<T>): Boolean {
        val uniResult = unify(x)
        return uniResult.isNotEmpty()
    }

    private fun unify(tree: INode<T>): List<MutableMap<Placeholder<T>, INode<T>>> {
        variables.forEach { it.t = null }
        return Unifier<T>().unify(tree, unificator)
    }

    override fun apply(x: INode<T>): INode<T> {
        val uniResults: List<Map<Placeholder<T>, INode<T>>> = unify(x)
        if (uniResults.isEmpty()) return x
        val uniResult = uniResults.first()
        for (i in variables.indices) {
            variables[i].t = uniResult[variables[i]]
        }
        val result = result()
        if (variables.any { it.t == null }) {
            throw IllegalStateException("")
        }
        for (c in DFS(result.toTree())) {
            if (!c.isLeaf()) {
                for (i in 0 until c.nodeSize()) {
                    val child = c.getChild(i)
                    if (child.isPlaceholder()) {
                        child as Placeholder<T>
                        if (child.t == null) {
                            throw IllegalStateException("")
                        }
                        c.setChild(i, child.t!!.element())
                    }
                }
            }
        }
        return if (result.element().isPlaceholder()) {
            val placeholder = result.element() as Placeholder<T>
            placeholder.t!!
        } else result
    }

    private val variables = ArrayList<Placeholder<T>>()

    init {
        for (c in DFS(unificator.toTree()).map { it.element() }.filterIsInstance<Placeholder<T>>()) {
            if (!variables.contains(c)) variables.add(c)
        }
        for (c in DFS(result().toTree()).map { it.element() }.filterIsInstance<Placeholder<T>>()) {
            if (!variables.contains(c)) throw IllegalArgumentException(
                "Rule $this uses variable ${c} in " +
                        "its transformed term but it never appears in the precondition term"
            )
        }
    }

    override fun toString(): String {
        try {
            return "$unificator -> ${result()}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}

interface IRule<T, R> {
    fun applicable(x: T): Boolean
    fun apply(x: T): R
}
