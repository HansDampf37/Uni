package analysis.terms.simplifying

import algo.datastructures.INode
import analysis.terms.*
import propa.IRule

interface ISimplifier {
    fun simplify(t: Term): Term

    fun simplifySingle(term: Term): List<Triple<Term, Double, IRule<INode<Term>, INode<Term>>>> {
        val rules = when (term) {
            is Sum -> RuleBook.sumRules
            is Product -> RuleBook.productRules
            is Power -> RuleBook.powerRules
            is Log -> RuleBook.logRules
            else -> throw NotImplementedError()
        }
        return simplifyWithRules(rules, term)
    }

    fun simplifyNumbersSingle(term: Term): List<Triple<Term, Double, IRule<INode<Term>, INode<Term>>>> =
        simplifyWithRules(RuleBook.numericalRules, term)

    fun simplifyFlattenSingle(term: Term): List<Triple<Term, Double, IRule<INode<Term>, INode<Term>>>> =
        simplifyWithRules(RuleBook.flattenRules, term)

    fun simplifyWithRules(
        rules: List<IRule<INode<Term>, INode<Term>>>,
        term: Term
    ): List<Triple<Term, Double, IRule<INode<Term>, INode<Term>>>> {
        return rules.filter { it.applicable(term) }.map {
            val result = it.apply(term).element()
            Triple(result, result.quality(), it)
        }
    }

    fun simplifyComponents(t: Term) {
        for (i in 0 until t.nodeSize()) t.setNode(i, simplify(t.getNode(i).element()))
    }
}