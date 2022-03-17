package propa

interface Placeholder: UnifyingTree {
    fun name(): String
    fun getT(): UnifyingTree?
    fun setT(t: UnifyingTree?)

    override fun getComponents() = throw NoComponents(this)
    override fun componentOrderMatters(): Boolean = throw NoComponents(this)

    override fun isComponent(): Boolean = true
    override fun equals(other: Any?): Boolean

    fun assign(tNew: UnifyingTree) {
        if (getT() != null) {
            if (tNew != getT()) throw UnifyingFailedException(this, tNew)
        }
        setT(tNew)
    }

    fun empty() {
        setT(null)
    }

    class NoComponents(unifyingTree: UnifyingTree) :
        IllegalArgumentException("$unifyingTree has no components")

    class UnifyingFailedException(placeholder: Placeholder, unifyingTree: UnifyingTree) :
        Throwable("$placeholder currently is unified to ${placeholder.getT()} and can therefore not be unified to $unifyingTree since $unifyingTree != ${placeholder.getT()}")
}
