package analysis

interface GroupAdd<T> {
    operator fun plus(other: T): T
    operator fun minus(other: T): T = plus(inverseAdd(other))
    fun inverseAdd(e: T): T

    fun zero(): T
}

fun <T> T.abs(): T where T : GroupAdd<T>, T : Comparable<T> {
    return if (this > this.zero()) this else this.zero() - this
}

operator fun <T: GroupAdd<T>> T.unaryMinus(): T {
    return inverseAdd(this)
}

fun <T: GroupAdd<T>> T.inverseAdd(): T {
    return inverseAdd(this)
}

fun <T: GroupMult<T>> T.inverseMult(): T {
    return inverseMult(this)
}

fun <T : GroupAdd<T>> List<T>.sum(): T {
    var sum = get(0)
    for (i in 1 until size) {
        sum += get(i)
    }
    return sum
}
