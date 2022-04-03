package propa

import algo.datastructures.INode

open class Placeholder<T: Unifiable>(
    protected val name: String,
    val constraint: (INode<T>) -> Boolean = { true },
    var subtree: INode<T>? = null,
    val filler: Boolean = false
) : Unifiable, Cloneable {

    override fun isUnifiableWith(unifiable: Unifiable): Boolean = true
    override fun isCommutative(): Boolean = false
    override fun isAssociative(): Boolean = false
    override fun isPlaceholder(): Boolean = true

    override fun equals(other: Any?): Boolean {
        return other is Placeholder<*> && other.name == name && other.subtree == subtree && constraint == other.constraint
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (subtree?.hashCode() ?: 0)
        return result
    }
}
