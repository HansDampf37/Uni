package analysis.terms.simplifying

import analysis.terms.Term

object Cache {
    private val size: Int = 100
    private val cache: MutableList<Pair<Term, Term>> = ArrayList()

    fun get(t: Term): Term? {
        val i = cache.indexOfFirst { it.first == t }
        if (i == -1) {
            return null
        }
        val el = cache.removeAt(i)
        cache.add(0, el)
        return el.second
    }

    fun add(key: Term, value: Term) {
        cache.add(0, Pair(key, value))
        if (cache.size > size) {
            cache.removeLast()
        }
    }
}