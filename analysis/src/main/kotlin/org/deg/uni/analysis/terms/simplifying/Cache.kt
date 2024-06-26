package org.deg.uni.analysis.terms.simplifying

import org.deg.uni.analysis.functions.Equation
import org.deg.uni.analysis.terms.model.Term

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

object CacheEqn {
    private val size: Int = 100
    private val cache: MutableList<Pair<Equation, Equation>> = ArrayList()

    fun get(t: Equation): Equation? {
        val i = cache.indexOfFirst { it.first == t }
        if (i == -1) {
            return null
        }
        val el = cache.removeAt(i)
        cache.add(0, el)
        return el.second
    }

    fun add(key: Equation, value: Equation) {
        cache.add(0, Pair(key, value))
        if (cache.size > size) {
            cache.removeLast()
        }
    }
}