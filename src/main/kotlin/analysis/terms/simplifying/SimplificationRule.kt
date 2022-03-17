package analysis.terms.simplifying

import analysis.terms.Term
import analysis.terms.flatten
import analysis.terms.pullUp
import analysis.terms.simplify
import propa.Placeholder
import propa.UnifyingTree

class SimplificationRule(private val precondition: Term, vararg variables: UnificationVariable, val transform: () -> Term) {
    private val variables = variables.toList()
    fun applyIfPossible(term: Term): Term {
        val uniResult = preconditionFulfilled(term)
        if (uniResult.first) {
            uniResult.second.forEach { it.key.t = it.value }
            val simplify = transform().pullUp()
            println("$term -> $simplify since: $this")
            return simplify
        }
        return term
    }

    fun preconditionFulfilled(term: Term): Pair<Boolean, MutableMap<Placeholder, UnifyingTree>> {
        variables.forEach { it.empty() }
        val unify = term.unify(precondition)
        return if (unify.isNotEmpty()) Pair(true, unify.first()) else Pair(false, mutableMapOf())
    }

    override fun toString(): String {
        return "$precondition -> ${transform()}"
    }
}
