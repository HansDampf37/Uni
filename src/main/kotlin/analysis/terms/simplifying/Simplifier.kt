package analysis.terms.simplifying

import analysis.terms.*

fun Term.quality(): Double {
    return TreeQuality2().calc(this)
}

class Simplifier : ISimplifier {
    private var N: Int = -1
    private var K: Int = -1
    private val cache: Cache = Cache

    override fun simplify(t: Term): Term {
        val simplified = cache.get(t)
        if (simplified != null) return simplified
        val quality = 9 * t.quality()
        N = -quality.toInt()
        K = 12 - quality.toInt()

        var listOfTerms = listOf(Triple(t, t.quality(), Rule(zero) { zero }))
        var best = Triple(t, t.quality(), Rule(zero) { zero })
        repeat(N) {
            listOfTerms.forEach { simplifyComponents(it.first) }

            var newListOfTerms = simplifyNumbers(listOfTerms)
            if (newListOfTerms.isNotEmpty()) {
                listOfTerms = newListOfTerms
                best = updateBest(listOfTerms, best)
                if (best.second == 0.0) return best.first
            }

            newListOfTerms = simplify(listOfTerms)
            if (newListOfTerms.isNotEmpty()) {
                listOfTerms = newListOfTerms
                best = updateBest(listOfTerms, best)
                if (best.second == 0.0) return best.first
            }

            newListOfTerms = simplifyFlatten(listOfTerms)
            if (newListOfTerms.isNotEmpty()) {
                listOfTerms = newListOfTerms
                best = updateBest(listOfTerms, best)
                if (best.second == 0.0) return best.first
            }

            if (listOfTerms.isEmpty()) return best.first
            best = updateBest(listOfTerms, best)
        }
        cache.add(t, best.first)
        return best.first
    }

    private fun updateBest(
        l: List<Triple<Term, Double, Rule>>,
        best: Triple<Term, Double, Rule>
    ): Triple<Term, Double, Rule> {
        return if (best.second > l[0].second) best else l[0]
    }

    private fun simplify(l: List<Triple<Term, Double, Rule>>): List<Triple<Term, Double, Rule>> {
        return sortAndCutTerms(l.map { term -> simplifySingle(term.first) }.flatten())
    }

    private fun simplifyNumbers(l: List<Triple<Term, Double, Rule>>): List<Triple<Term, Double, Rule>> {
        return sortAndCutTerms(l.map { term -> simplifyNumbersSingle(term.first) }.flatten())
    }

    private fun simplifyFlatten(l: List<Triple<Term, Double, Rule>>): List<Triple<Term, Double, Rule>> {
        return sortAndCutTerms(l.map { term -> simplifyFlattenSingle(term.first) }.flatten())
    }

    private fun sortAndCutTerms(terms: List<Triple<Term, Double, Rule>>) =
        terms.sortedByDescending { it.second }.slice(0 until minOf(K, terms.size))
}
