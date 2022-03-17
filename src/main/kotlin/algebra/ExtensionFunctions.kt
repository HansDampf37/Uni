package algebra

import analysis.GroupAdd

fun <T, Q, S> List<T>.zipWith(other: Iterable<Q>, operation: (one: T, two: Q) -> S): List<S> {
    val zip: List<Pair<T, Q>> = zip(other)
    return zip.map { p: Pair<T, Q> -> operation(p.first, p.second) }
}

fun addPadding(str: String, wantedSize: Int, paddingChar: Char): String {
    if (wantedSize < str.length) throw IllegalArgumentException()
    val pad = CharArray(wantedSize - str.length) { paddingChar }
    return String(pad) + str
}
