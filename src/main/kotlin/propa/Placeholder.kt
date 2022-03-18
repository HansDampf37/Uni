package propa

open class Placeholder(
    protected val name: String,
    val constraint: (UnifyingTree) -> Boolean = { true },
    var t: UnifyingTree? = null,
    val temporary: Boolean = false,
    val filler: Boolean = false
) : UnifyingTree {
    override fun getComponents() = throw NoComponents(this)
    override fun nonCommutativeComponents(): Boolean = throw NoComponents(this)
    override fun isComponent(): Boolean = true
    override fun addComponent(c: UnifyingTree) = throw NoComponents(this)
    override fun removeComponent(c: UnifyingTree) = throw NoComponents(this)
    override fun init(): UnifyingTree = throw IllegalCallerException()

    override fun equals(other: Any?): Boolean {
        return other is Placeholder && other.name == name && other.t == t && constraint == other.constraint
    }

    override fun clone(): UnifyingTree = Placeholder(name, constraint, t)

    fun assign(tNew: UnifyingTree) {
        if (t != null) {
            if (tNew != t) throw UnifyingFailedException(this, tNew)
        }
        t = tNew
    }

    fun empty() {
        t = null
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (t?.hashCode() ?: 0)
        return result
    }

    class NoComponents(unifyingTree: UnifyingTree) :
        IllegalArgumentException("$unifyingTree has no components")

    class UnifyingFailedException(placeholder: Placeholder, unifyingTree: UnifyingTree) :
        Throwable("$placeholder currently is unified to ${placeholder.t} and can therefore not be unified to $unifyingTree since $unifyingTree != ${placeholder.t}")
}
