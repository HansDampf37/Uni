package analysis

/**
 * Implementing methods have to implement the operators + and - as well as a zero and an inversion method.
 *
 * @param T
 * @constructor Create empty Group add
 */
interface GroupAdd<T> {
    operator fun plus(other: T): T
    operator fun minus(other: T): T
    fun inverseAdd(): T

    fun zero(): T
}

fun <T> T.abs(): T where T : GroupAdd<T>, T : Comparable<T> {
    return if (this > this.zero()) this else this.zero() - this
}

operator fun <T: GroupAdd<T>> T.unaryMinus(): T {
    return this.inverseAdd()
}

fun <T: GroupAdd<T>> T.inverseAdd(): T {
    return this.inverseAdd()
}

fun <T: GroupMult<T>> T.inverseMult(): T {
    return this.inverseMult()
}

fun <T : GroupAdd<T>> List<T>.sum(): T {
    var sum = get(0)
    for (i in 1 until size) {
        sum += get(i)
    }
    return sum
}
