package propa

interface UnifyingTree : Cloneable {
    fun getComponents(): List<UnifyingTree>
    fun nonCommutativeComponents(): Boolean
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
            // might still unify multiple components into one Placeholder but only if order doesn't matter
            if (nonCommutativeComponents()) return listOf()
            if (ownComponents.size < unificationComponents.size) return listOf()
            if (unificationComponents.none { it is Placeholder && it.filler }) return listOf()
            return unifyAllPermutations(ownComponents, unificationComponents)
        }
        return if (nonCommutativeComponents()) unifyComponents(ownComponents, unificationComponents)
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

    /**
     * If [ownComponents] is longer than [unificationComponents] merges additional components of [ownComponents] into
     * a new [UnifyingTree] and replaces additional components.
     * The returned lists have equal length.
     *
     * Unificator x, f, y (where f is filler)
     * tree a, b, c, d, e
     * -> x, y, f
     * -> a, b, (c, d, e)
     * additionalElements = uni.size - 1 until tree.size
     * ownNew = 0 until tree.size - uni.size
     *
     * Caller must assure that
     * - [nonCommutativeComponents] == false
     * - [ownComponents].size > [unificationComponents].size
     * - [unificationComponents] contains at least one Placeholder p with [p.filler][Placeholder.filler] == true
     *
     * @param ownComponents
     * @param unificationComponents
     * @return Pair(ownComponents', unificationComponents)
     * @throws IllegalCallerException if one of the three conditions is violated
     */
    fun unifyDifferentSize(
        ownComponents: List<UnifyingTree>,
        unificationComponents: List<UnifyingTree>
    ): Pair<List<UnifyingTree>, List<UnifyingTree>> {
        if (nonCommutativeComponents()) throw IllegalCallerException()
        if (ownComponents.size < unificationComponents.size) throw IllegalCallerException()
        if (unificationComponents.none { it is Placeholder && it.filler }) throw IllegalCallerException()
        // merge additional components in one filler

        // create new unificator with filler at the end
        val filler = unificationComponents.first { it is Placeholder && it.filler }
        val uniMod = unificationComponents.toMutableList()
        val unificator: UnifyingTree = init()
        uniMod.remove(filler)
        uniMod.add(filler) // the filler is now at the end
        uniMod.forEach { unificator.addComponent(it) }

        // create new UnifyingTree that contains additional components
        val additionalOwnComps = ownComponents.slice(unificationComponents.size - 1 until ownComponents.size)
        val subTree: UnifyingTree = init()
        additionalOwnComps.forEach { subTree.addComponent(it) }

        // create a New Unifying tree that contains the subtree instead of the additional components
        val ownMod = ownComponents.slice(0 until ownComponents.size - uniMod.size).toMutableList()
        ownMod.add(subTree)
        return Pair(ownMod, uniMod)
    }

    fun init(): UnifyingTree

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
        val (own, uni) = if (ownComponents.size == unificationComponents.size) {
            Pair(ownComponents, unificationComponents)
        } else if (ownComponents.size > unificationComponents.size) {
            unifyDifferentSize(ownComponents, unificationComponents)
        } else {
            return listOf()
        }
        val subResults: List<List<MutableMap<Placeholder, UnifyingTree>>> = List(own.size) { i -> own[i].unify(uni[i]) }
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

