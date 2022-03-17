package analysis

interface GroupMult<T> {
    operator fun times(other: T): T
    operator fun div(other: T): T = times(inverseMult(other))
    fun inverseMult(e: T): T

    fun one(): T
}

fun <T : GroupMult<T>> List<T>.mult(): T {
    var sum = get(0)
    for (i in 1 until size) {
        sum *= get(i)
    }
    return sum
}
