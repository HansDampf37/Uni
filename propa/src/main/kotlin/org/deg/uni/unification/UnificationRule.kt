package org.deg.uni.unification

import org.deg.uni.graphs.datastructures.DFS
import org.deg.uni.graphs.datastructures.INode

class UnificationRule<T : Unifiable>(val unificator: INode<T>, val result: () -> INode<T>) : IRule<INode<T>, INode<T>> {
    override fun applicable(x: INode<T>): Boolean {
        val uniResult = unify(x)
        return uniResult.isNotEmpty()
    }

    private fun unify(tree: INode<T>): List<MutableMap<Placeholder<T>, INode<T>>> {
        variables.forEach { it.subtree = null }
        return Unifier<T>().unify(tree, unificator)
    }

    override fun apply(x: INode<T>): INode<T> {
        val uniResults: List<Map<Placeholder<T>, INode<T>>> = unify(x)
        if (uniResults.isEmpty()) return x
        val uniResult = uniResults.first()
        for (i in 0 until variables.size) {
            variables[i].subtree = uniResult[variables[i]]
        }
        val result = result()
        for (c in DFS(result.toTree())) {
            if (!c.isLeaf()) {
                for (i in 0 until c.nodeSize()) {
                    val child = c.getChild(i)
                    if (child.isPlaceholder()) {
                        child as Placeholder<T>
                        if (child.subtree == null) {
                            throw IllegalStateException("Placeholder $child has not gotten assigned the subtree ${uniResult[child]}")
                        }
                        c.setChild(i, child.subtree!!.element())
                    }
                }
            }
        }
        return if (result.element().isPlaceholder()) {
            val placeholder = result.element() as Placeholder<T>
            placeholder.subtree!!
        } else result
    }

    private val variables = ArrayList<Placeholder<T>>()

    init {
        for (c in DFS(unificator.toTree()).map { it.element() }.filterIsInstance<Placeholder<T>>()) {
            if (!variables.contains(c)) variables.add(c)
        }
        for (c in DFS(result().toTree()).map { it.element() }.filterIsInstance<Placeholder<T>>()) {
            if (!variables.contains(c)) throw IllegalArgumentException(
                "Rule $this uses variable $c in " +
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

class SubTreeUnificationRule<T : Unifiable>(private val rule: UnificationRule<T>) : IRule<INode<T>, INode<T>> {
    override fun applicable(x: INode<T>): Boolean {
        for (child in DFS(x.toTree())) {
            if (rule.applicable(child)) return true
        }
        return false
    }

    override fun apply(x: INode<T>): INode<T> {
        val result = x.clone()
        for (element in DFS(result.toTree())) {
            for (i in 0 until element.nodeSize()) {
                val child = element.getNode(i)
                if (rule.applicable(child)) {
                    val applied = rule.apply(child)
                    element.setNode(i, applied)
                }
            }
        }
        return result
    }

    override fun toString(): String {
        return "some term ($rule)"
    }
}

interface IRule<T, R> {
    fun applicable(x: T): Boolean
    fun apply(x: T): R
}
