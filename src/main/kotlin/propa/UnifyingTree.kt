package propa

interface UnifyingTree {
    fun getComponents(): List<UnifyingTree>
    fun componentOrderMatters(): Boolean
    fun isComponent(): Boolean
    override fun equals(other: Any?): Boolean

    fun unify(unification: UnifyingTree): List<MutableMap<Placeholder, UnifyingTree>> {
        if (unification.isComponent()) {
            return when (unification) {
                is Placeholder -> listOf(mutableMapOf(Pair(unification, this)))
                this -> listOf(mutableMapOf())
                else -> listOf()
            }
        }
        if (this.javaClass != unification.javaClass) return listOf()
        val ownComponents = getComponents()
        val unificationComponents = unification.getComponents()
        if (ownComponents.size != unificationComponents.size) return listOf()
        return if (componentOrderMatters()) unifyComponents(ownComponents, unificationComponents)
        else {
            val permutations = Permutations(ownComponents)
            permutations.map {
                //unify each permutation and merge all solutions
                unifyComponents(it, unificationComponents)
            }.flatten()
        }
    }

    private fun unifyComponents(
        ownComponents: List<UnifyingTree>,
        unificationComponents: List<UnifyingTree>
    ): List<HashMap<Placeholder, UnifyingTree>> {
        val finalSolution = ArrayList<HashMap<Placeholder, UnifyingTree>>()
        val subResults = List(ownComponents.size) { i -> ownComponents[i].unify(unificationComponents[i])}
        // resultSorted[i] contains all solutions of component i as List<HashMap<Placeholder, UnifyingTree>>
        val resultSorted = subResults.sortedBy { it.size }
        if (subResults[0].size == 2) {
            println("breakpoint")
        }

        if (resultSorted[0].isEmpty()) return listOf()

        val notTriedMask: MutableList<Long> = MutableList(resultSorted.size) { -1L }
        val availableMask = MutableList(resultSorted.size) {-1L }

        var level = 0
        //contents are going to be overwritten
        val solution: MutableList<MutableMap<Placeholder, UnifyingTree>> = MutableList(resultSorted.size) { HashMap() }
        val indices = MutableList(resultSorted.size) { -1 }

        while (level >= 0) {
            if (level == resultSorted.size) {
                // solution
                finalSolution.add(merge(solution))
                availableMask[level - 1] = availableMask[level - 1] xor (1L shl indices[level - 1])
                level--
                continue
            }
            val nextIndex = (notTriedMask[level] and availableMask[level]).countTrailingZeroBits()
            if (nextIndex == resultSorted[level].size) {
                // no further solution
                if (level == 0) break
                if (indices[level] >= 0) availableMask[level] = availableMask[level] xor (1L shl indices[level])
                notTriedMask[level--] = -1L
                continue
            }
            notTriedMask[level] = notTriedMask[level] xor (1L shl nextIndex)
            if (newSolutionMatchesWithOldSolutions(solution, resultSorted[level][nextIndex], level)) {
                availableMask[level] = availableMask[level] xor (1L shl nextIndex)
                solution[level] = resultSorted[level][nextIndex]
                indices[level] = nextIndex
                level++
            }
        }
        /*for (i in ownComponents.indices) {
            val result = ownComponents[i].unify(unificationComponents[i])
            if (!result.first) return Pair(false, HashMap())
            // could unify subterms
            for (unification in result.second) {
                if (solution[unification.key] == null) {
                    // placeholder is not yet unified
                    solution[unification.key] = unification.value
                } else {
                    // placeholder already got unified
                    if (solution[unification.key] != unification.value) return Pair(false, HashMap())
                    // placeholder remains consistent
                }
            }
        }
        return Pair(true, solution)*/
        return finalSolution
    }

    fun newSolutionMatchesWithOldSolutions(
        solution: MutableList<MutableMap<Placeholder, UnifyingTree>>,
        mutableMap: MutableMap<Placeholder, UnifyingTree>,
        level: Int
    ): Boolean {
        for (e in mutableMap){
            for (i in 0 until level) {
                if (solution[i][e.key] != null && solution[i][e.key] != e.value) {
                    return false
                }
            }
        }
        return true
    }

    fun merge(solution: MutableList<MutableMap<Placeholder, UnifyingTree>>): java.util.HashMap<Placeholder, UnifyingTree> {
        val res = HashMap<Placeholder, UnifyingTree>()
        for (s in solution) {
            for (e in s) {
                res[e.key] = e.value
            }
        }
        return res
    }
}

