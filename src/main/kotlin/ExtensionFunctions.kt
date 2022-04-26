import algo.datastructures.INode
import analysis.terms.model.Num
import analysis.terms.model.Term
import analysis.terms.parsing.LexiAnalysis
import analysis.terms.parsing.SyntacticAnalysis

fun String.toTerm(): Term {
    val tokensAndAssignment = LexiAnalysis().parse(this)
    return SyntacticAnalysis().parse(tokensAndAssignment.first, tokensAndAssignment.second)
}

fun <T> INode<T>.initFromList(subNodes: List<INode<T>>): INode<T> {
    return this.clone().apply {
        for (i in (0 until nodeSize()).reversed()) removeNodeAt(i)
        subNodes.forEach { addNode(it) }
    }
}

fun String.toNum(): Num {
    if (contains("/")) {
        val parts = split("/")
        val num = parts[0].toDouble()
        val denom = parts[1].toDouble()
        return Num(num, denom)
    }
    return Num(toDouble())
}

fun Int.factorial(): Long {
    if (this < 0) throw ArithmeticException("factorial of a negative number")
    var cur = 1L
    for (i in 2..this) {
        cur *= i
    }
    return cur
}

operator fun <T> Iterable<T>.plus(other: Iterable<T>): MutableList<T> {
    return ArrayList<T>().apply {
        addAll(other)
        addAll(this)
    }
}

fun <T> List<T>.permute(): List<List<T>> {
    if (this.isEmpty()) return listOf()
    val excluded = Array(this.size) { false }
    val tried = Array(this.size) { Array(this.size) { false } }
    val currentIndices = Array(this.size) { -1 }
    val results = mutableListOf<List<T>>()
    val result = MutableList(this.size) { i -> this[i] }
    var depth = 0
    while (depth >= 0) {
        if (depth == this.size) {
            // solution
            results.add(result.toList())
            excluded[currentIndices[depth - 1]] = false
            depth--
            continue
        }
        val index = this.indices.firstOrNull {
            !excluded[it] && !tried[depth][it]
        }
        // no element left for this depth
        if (index == null) {
            // no further solution
            if (depth == 0) break
            excluded[currentIndices[depth - 1]] = false
            for (i in tried[depth].indices) tried[depth][i] = false
            depth--
            continue
        }
        tried[depth][index] = !tried[depth][index]
        excluded[index] = true
        currentIndices[depth] = index
        result[depth] = this[index]
        depth++
    }
    return results
}

typealias Partition<T> = List<SubList<T>>
typealias SubList<T> = List<T>

fun <T> List<T>.partition(amountOfSubsets: Int): List<Partition<T>> {
    assert(amountOfSubsets >= 1)
    if (amountOfSubsets == 1) return listOf(listOf(this))
    val partitions = ArrayList<Partition<T>>()
    for (i in 0 until size - amountOfSubsets + 1) {
        val firstSubset: List<T> = this.slice(0..i)
        val otherSubsets: List<Partition<T>> = this.slice(i + 1 until size).partition(amountOfSubsets - 1)
        otherSubsets.forEach { partitions.add(mutableListOf(firstSubset).apply { addAll(it) }) }
    }
    return partitions
}

fun <T> List<T>.partition(): List<Partition<T>> {
    return IntRange(1, size).map { partition(it) }.flatten()
}

fun <T> List<T>.partitionPerm(): List<Partition<T>> {
    val permutations = permute()
    return IntRange(1, size).map { n: Int ->
        permutations.map { permuted: List<T> ->
            permuted.partition(n)
        }.flatten()
    }.flatten().toMutableList().removeDuplicates()
}

private fun <E> MutableList<E>.removeDuplicates(): MutableList<E> {
    this.sortedBy { it.hashCode() }
    for (i in (0 until size - 1).reversed()) {
        if (this[i] == this[i + 1]) removeAt(i + 1)
    }
    return this
}

fun <T> List<T>.subsets(): List<List<T>> {
    if (this.isEmpty()) return listOf()
    val excluded = Array(this.size) { false }
    val tried = Array(this.size) { Array(this.size) { false } }
    val currentIndices = Array(this.size) { -1 }
    val results = mutableListOf<List<T>>()
    val result = MutableList(this.size) { i -> this[i] }
    var depth = 0
    while (depth >= 0) {
        if (depth == this.size) {
            excluded[currentIndices[depth - 1]] = false
            depth--
            continue
        }
        val index = this.indices.firstOrNull {
            !excluded[it] && !tried[depth][it]
        }
        // no element left for this depth
        if (index == null) {
            // no further solution
            if (depth == 0) break
            excluded[currentIndices[depth - 1]] = false
            for (i in tried[depth].indices) tried[depth][i] = false
            depth--
            continue
        }
        tried[depth][index] = !tried[depth][index]
        excluded[index] = true
        currentIndices[depth] = index
        result[depth] = this[index]
        results.add(result.toList().slice(0..depth))
        depth++
    }
    return results.apply { add(listOf()) }
}