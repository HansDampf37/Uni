package propa

import java.lang.Integer.min

class KontextFreeGrammar(val terminals: List<String>, val notTerminals: List<String>, start: String, rules: List<Pair<Regex, String>>) {

    init {
        if (terminals.any { notTerminals.contains(it) }) throw IllegalArgumentException("Terminals and NotTerminals must be disjunkt")
        if (notTerminals.any { terminals.contains(it) }) throw IllegalArgumentException("Terminals and NotTerminals must be disjunkt")
        // if (rules.any { !terminals.contains(it.second) && !notTerminals.contains(it.second) }) throw IllegalArgumentException("Rules must produce Terminals and not terminals.")
    }

    fun cyk(word: String): Boolean {
        TODO()
    }

    fun k_Start(k: Int, word: String) = word.slice(IntRange(0, min(k, word.length)))

    fun first(k: Int, word: String) {
        if (!notTerminals.contains(word)) throw IllegalArgumentException("$word is not in NotTerminals")

    }
}