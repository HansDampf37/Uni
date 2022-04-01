package analysis.terms.simplifying

import algo.datastructures.DFS
import algo.datastructures.INode
import analysis.terms.Term
import propa.Placeholder
import propa.UnifyingTree

class SimplificationRule(private val precondition: Term, val transform: () -> Term) {
    private val variables = ArrayList<UnificationVariable>()

    init {
        for (c in DFS(precondition.toTree()).filterIsInstance<UnificationVariable>()) {
            if (!variables.contains(c)) variables.add(c)
        }
        for (c in DFS(transform().toTree()).filterIsInstance<UnificationVariable>()) {
            if (!variables.contains(c.get())) throw IllegalArgumentException("Rule $this uses variable ${c.get()} in " +
                    "its transformed term but it never appears in the precondition term")
        }
    }

    fun apply(term: Term): Term {
        val uniResult = applicable(term)
        if (uniResult.first) {
            for (i in variables.indices) {
                variables[i].t = uniResult.second[variables[i]]
            }
            val result = transform()
            if (variables.any { it.value == null }) {
                throw IllegalStateException("")
            }
            for (c in DFS(result.toTree())) {
                if (!c.isLeaf()) {
                    for (i in 0 until c.nodeSize()) {
                        val node = c.getNode(i)
                        if (node is UnificationVariable) {
                            if (node.t == null) {
                                throw IllegalStateException("")
                            }
                            c.setNode(i, node.t as Term)
                        }
                    }
                }
            }
            return if (result is UnificationVariable) result.t as Term else result
        }
        return term
    }

    fun applicable(term: Term): Pair<Boolean, MutableMap<Placeholder, UnifyingTree>> {
        variables.forEach { it.empty() }
        val unify = term.unify(precondition)
        return if (unify.isNotEmpty()) Pair(true, unify.first()) else Pair(false, mutableMapOf())
    }

    override fun toString(): String {
        try {
            return "$precondition -> ${transform()}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}
