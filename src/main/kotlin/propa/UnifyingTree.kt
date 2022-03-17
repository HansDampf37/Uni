package propa

interface UnifyingTree {
    fun getComponents(): List<UnifyingTree>
    fun componentOrderMatters(): Boolean
    fun isComponent(): Boolean
    override fun equals(other: Any?): Boolean

    fun unify(unification: UnifyingTree): Pair<Boolean, MutableMap<Placeholder, UnifyingTree>> {
        if (unification.isComponent()) return Pair(true, mutableMapOf(Pair(unification as Placeholder, this)))
        if (this.javaClass != unification.javaClass) return Pair(false, HashMap())
        val ownComponents = getComponents()
        val unificationComponents = unification.getComponents()
        if (ownComponents.size != unificationComponents.size) return Pair(false, HashMap())
        if (componentOrderMatters()) return unifyComponents(ownComponents, unificationComponents)
        else {
            return unifyComponents(ownComponents, unificationComponents)
        }
    }

    private fun unifyComponents(
        ownComponents: List<UnifyingTree>,
        unificationComponents: List<UnifyingTree>
    ): Pair<Boolean, HashMap<Placeholder, UnifyingTree>> {
        val solution = HashMap<Placeholder, UnifyingTree>()
        for (i in ownComponents.indices) {
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
        return Pair(true, solution)
    }
}