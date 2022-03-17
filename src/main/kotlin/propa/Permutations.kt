package propa

class Permutations<T>(val l: List<T>) : Iterable<List<T>>, Cloneable {
    init {
        if (l.size >= 64) throw IllegalArgumentException("List to big")
    }

    override fun iterator(): Iterator<List<T>> {
        return object : Iterator<List<T>> {
            val permutations: ArrayList<List<T>> = ArrayList()
            var i = 0

            init {
                val notTriedMask: MutableList<Long> = MutableList(l.size) { -1L }
                var availableMask: Long = -1L
                var level = 0
                val permutation = MutableList(l.size) { i -> l[i] }
                val indices = MutableList(l.size) { -1 }
                while (level >= 0) {
                    if (level == l.size) {
                        // solution
                        permutations.add(permutation.toList())
                        availableMask = availableMask xor (1L shl indices[level - 1])
                        level--
                        continue
                    }
                    val nextIndex = (notTriedMask[level] and availableMask).countTrailingZeroBits()
                    if (nextIndex == l.size) {
                        // no further solution
                        if (level == 0) break
                        availableMask = availableMask xor (1L shl indices[level - 1])
                        notTriedMask[level--] = -1L
                        continue
                    }
                    notTriedMask[level] = notTriedMask[level] xor (1L shl nextIndex)
                    availableMask = availableMask xor (1L shl nextIndex)
                    permutation[level] = l[nextIndex]
                    indices[level] = nextIndex
                    level++
                }
            }

            override fun hasNext(): Boolean = i < permutations.size

            override fun next(): List<T> = permutations[i++]
        }
    }
}