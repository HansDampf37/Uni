package propa

interface UnifyingTree : Cloneable {
    fun getComponents(): List<UnifyingTree>
    fun componentOrderMatters(): Boolean
    fun isComponent(): Boolean
    fun addComponent(c: UnifyingTree)
    fun removeComponent(c: UnifyingTree)
    override fun equals(other: Any?): Boolean

    fun unify(unification: UnifyingTree): List<MutableMap<Placeholder, UnifyingTree>> {
        if (unification.isComponent()) return unifyComponent(unification)
        if (this.javaClass != unification.javaClass) return listOf()
        val ownComponents = getComponents()
        val unificationComponents = unification.getComponents()
        if (ownComponents.size != unificationComponents.size) {
            return listOf()
            // might still unify multiple components into one Placeholder but only if order doesn't matter
            // return unifyDifferentSize(ownComponents, unificationComponents, unification)
        }
        return if (componentOrderMatters()) unifyComponents(ownComponents, unificationComponents)
        else unifyAllPermutations(ownComponents, unificationComponents)
    }

    fun unifyAllPermutations(
        ownComponents: List<UnifyingTree>,
        unificationComponents: List<UnifyingTree>
    ): List<HashMap<Placeholder, UnifyingTree>> {
        val permutations = Permutations(ownComponents)
        return permutations.map {
            //unify each permutation and merge all solutions
            unifyComponents(it, unificationComponents)
        }.flatten()
    }

    fun unifyDifferentSize(
        ownComponents: List<UnifyingTree>,
        unificationComponents: List<UnifyingTree>,
        unification: UnifyingTree
    ): List<MutableMap<Placeholder, UnifyingTree>> {
        if (componentOrderMatters()) return listOf()
        if (ownComponents.size < unificationComponents.size) {
            val temporary = ownComponents.filter { it is Placeholder && it.temporary }
            return if (ownComponents.size - temporary.size == unificationComponents.size) {
                for (temp in temporary) unification.removeComponent(temp)
                unify(unification)
            } else {
                listOf()
            }
        }
        // introduce new placeholders
        repeat(ownComponents.size - unificationComponents.size) { i ->
            unification.addComponent(Placeholder("Temp$i", { true }, null, true))
        }
        return unify(unification)
    }

    fun unifyComponent(unification: UnifyingTree): List<MutableMap<Placeholder, UnifyingTree>> {
        return when (unification) {
            is Placeholder -> {
                if (unification.constraint(this)) listOf(mutableMapOf(Pair(unification, this)))
                else listOf()
            }
            this -> listOf(mutableMapOf())
            else -> listOf()
        }
    }

    private fun unifyComponents(
        ownComponents: List<UnifyingTree>,
        unificationComponents: List<UnifyingTree>
    ): List<HashMap<Placeholder, UnifyingTree>> {
        val finalSolution = ArrayList<HashMap<Placeholder, UnifyingTree>>()
        val subResults = List(ownComponents.size) { i -> ownComponents[i].unify(unificationComponents[i]) }
        // resultSorted[i] contains all solutions of component i as List<HashMap<Placeholder, UnifyingTree>>
        val resultSorted = subResults.sortedBy { it.size }
        if (resultSorted[0].isEmpty()) return listOf()

        val notTriedMask: MutableList<Long> = MutableList(resultSorted.size) { -1L }
        val availableMask = MutableList(resultSorted.size) { -1L }

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
        return finalSolution
    }

    fun newSolutionMatchesWithOldSolutions(
        solution: MutableList<MutableMap<Placeholder, UnifyingTree>>,
        mutableMap: MutableMap<Placeholder, UnifyingTree>,
        level: Int
    ): Boolean {
        for (e in mutableMap) {
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

    override fun clone(): UnifyingTree
}

