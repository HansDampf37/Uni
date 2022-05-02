package org.deg.uni.unification

import org.deg.uni.graphs.datastructures.INode

/**
 * A placeholder is a special leaf of a unificator. During [Unification][Unifier] alternative subtrees for all placeholders
 * are searched.
 *
 * @param T type parameter of the [tree][ITree]
 * @property name name of the Placeholder variable
 * @property constraint conditions that must be fulfilled by the assigned subtree.
 * @property subtree the assigned subtree
 * @property filler whether this Placeholder can consume multiple nodes
 */
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
