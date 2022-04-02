package analysis.terms.simplifying

import analysis.terms.*

interface ISimplifier {
    fun simplify(t: Term): Term

    fun simplifySingle(term: Term): List<Triple<Term, Double, Rule>> {
        val rules = when (term) {
            is Sum -> RuleBook.sumRules
            is Product -> RuleBook.productRules
            is Power -> RuleBook.powerRules
            is Log -> RuleBook.logRules
            else -> throw NotImplementedError()
        }
        return simplifyWithRules(rules, term)
    }

    fun simplifyNumbersSingle(term: Term): List<Triple<Term, Double, Rule>> =
        simplifyWithRules(RuleBook.numericalRules, term)

    fun simplifyFlattenSingle(term: Term): List<Triple<Term, Double, Rule>> =
        simplifyWithRules(RuleBook.flattenRules, term)

    fun simplifyWithRules(
        rules: List<Rule>,
        term: Term
    ): List<Triple<Term, Double, Rule>> {
        return rules.filter { it.applicable(term) }.map {
            val result = it.apply(term).element()
            Triple(result, result.quality(), it)
        }
    }

    fun simplifyComponents(t: Term) {
        for (i in 0 until t.nodeSize()) t.setNode(i, simplify(t.getNode(i).element()))
    }
}