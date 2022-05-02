package org.deg.uni.unification

import org.deg.uni.graphs.datastructures.INode

/**
 * A Unifier checks whether a given [INode<Unifiable>][INode] is unifiable with another [INode<Unifiable>][INode] called
 * unificator. A Tree is unifiable with a unificator if there is a way to assign subtrees to special leafs ([Placeholder])
 * in the unificator so that the unificator and the tree are equal. Two [commutative][Unifiable.isCommutative] nodes are
 * equal if they contain the same sub-nodes. If the nodes contained Unifiable indicates that they are not commutative,
 * the contained sub-nodes must be in the same order for them to be equal.
 * Examples:
 * - Tree: x(y1(y2), z)
 * - Unificator: x(y1(A), z) with Placeholder A
 *
 * is unifiable with A := y2
 * - Tree: x(y, z)
 * - Unificator: A with Placeholder A
 *
 * is unifiable with A := x(y, z)
 * - Tree: x(y1, y2)
 * - Unificator: x(z, A) with Placeholder A
 *
 * is not unifiable.
 *
 * In order to use unification in a more flexible way, Placeholders may consume more than one node if their
 * [fill-property][Placeholder.filler] is true. The following examples are unifiable if and only if Placeholder A has its
 * filler-attribute set to true.
 * - Tree: x(y, z) Unificator: x(A)
 * - Tree: x(y, z, q) Unificator: x(y, A)
 *
 * @param T
 * @constructor Create empty Unifier
 */
class Unifier<T: Unifiable> {

    /**
     * Returns all possible assignments for the [Placeholder] variables contained in the unificator that make the given [tree] and
     * [unificator] equal. Each element in the returned list is a possible solution of the unification problem. If the tree
     * and unificator are not unifiable, the returned list is empty.
     * @see Unifier
     *
     * @return all assignments for the [Placeholder] variable in the unificator
     */
    fun unify(tree: INode<T>, unificator: INode<T>): List<MutableMap<Placeholder<T>, INode<T>>> {
        if (unificator.isLeaf()) return unifyComponent(tree, unificator)
        if (!unificator.element().isUnifiableWith(tree.element())) return listOf()
        return if (!tree.element().isCommutative()) unifyComponents(tree, unificator)
        else unifyAllPermutations(tree, unificator)
    }

    private fun unifyAllPermutations(
        tree: INode<T>,
        unificator: INode<T>
    ): List<HashMap<Placeholder<T>, INode<T>>> {
        if (!tree.element().isCommutative()) throw IllegalCallerException()
        val permutations = Permutations(tree.subNodes())
        return permutations.map {
            //unify each permutation and merge all solutions
            unifyComponents(tree.initFromList(it), unificator)
        }.flatten()
    }

    private fun unifyDifferentSize(
        tree: INode<T>,
        unificator: INode<T>
    ): Pair<INode<T>, INode<T>> {
        if (!tree.element().isAssociative()) throw IllegalCallerException()
        if (tree.nodeSize() < unificator.nodeSize()) throw IllegalCallerException()
        if (unificator.subNodes().none { it.element() is Placeholder<*> && (it.element() as Placeholder<*>).filler }) throw IllegalCallerException()
        // merge additional components in one filler
        val fillerIndex = unificator.subNodes().indexOfFirst { it is Placeholder<*> && it.filler }
        val rangeBeforeFiller: IntRange = 0 until fillerIndex
        val rangeAfterFiller: IntRange = fillerIndex + 1 until unificator.nodeSize()
        val oldTreeComponents = tree.subNodes().toMutableList()
        val treeRangeAfterFiller = rangeAfterFiller.map { it + tree.nodeSize() - unificator.nodeSize() }
        val newTreeComponents = ArrayList<INode<T>>(unificator.nodeSize())
        for (i in treeRangeAfterFiller.reversed()) {
            val el = oldTreeComponents.removeAt(i)
            newTreeComponents.add(el)
        }
        for (i in rangeBeforeFiller.reversed()) {
            val el = oldTreeComponents.removeAt(i)
            newTreeComponents.add(el)
        }
        val fillerNodeForTree = tree.initFromList(oldTreeComponents)
        newTreeComponents.add(fillerIndex, fillerNodeForTree)
        val modifiedTree: INode<T> = tree.initFromList(newTreeComponents)
        return Pair(modifiedTree, unificator)
    }

    private fun unifyComponents(
        tree: INode<T>,
        unificator: INode<T>
    ): List<HashMap<Placeholder<T>, INode<T>>> {
        val finalSolution = ArrayList<HashMap<Placeholder<T>, INode<T>>>()
        var ownComponents = tree.subNodes()
        var unificationComponents = unificator.subNodes()
        if (ownComponents.size > unificationComponents.size && tree.element().isAssociative()) {
            if (unificator.subNodes().any { it.element() is Placeholder<*> && (it.element() as Placeholder<*>).filler }) {
                val result = unifyDifferentSize(tree, unificator)
                ownComponents = result.first.subNodes()
                unificationComponents = result.second.subNodes()
            } else {
                return emptyList()
            }
        } else if (ownComponents.size < unificationComponents.size) {
            return emptyList()
        }
        val subResults: List<List<MutableMap<Placeholder<T>, INode<T>>>> =
            List(ownComponents.size) { i -> unify(ownComponents[i], unificationComponents[i]) }
        // resultSorted[i] contains all solutions of component i as List<HashMap<Placeholder, UnifyingTree>>
        val resultSorted = subResults.sortedBy { it.size }
        if (resultSorted[0].isEmpty()) return emptyList()

        val notTriedMask: MutableList<Long> = MutableList(resultSorted.size) { -1L }
        val availableMask = MutableList(resultSorted.size) { -1L }

        var level = 0
        //contents are going to be overwritten
        val solution: MutableList<MutableMap<Placeholder<T>, INode<T>>> = MutableList(resultSorted.size) { HashMap() }
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

    private fun newSolutionMatchesWithOldSolutions(
        solution: MutableList<MutableMap<Placeholder<T>, INode<T>>>,
        mutableMap: MutableMap<Placeholder<T>, INode<T>>,
        level: Int
    ): Boolean {
        for (e in mutableMap) {
            for (i in 0 until level) {
                if (solution[i][e.key] != null && solution[i][e.key]?.element() != e.value.element()) {
                    return false
                }
            }
        }
        return true
    }

    private fun merge(solution: MutableList<MutableMap<Placeholder<T>, INode<T>>>): java.util.HashMap<Placeholder<T>, INode<T>> {
        val res = HashMap<Placeholder<T>, INode<T>>()
        for (s in solution) {
            for (e in s) {
                res[e.key] = e.value
            }
        }
        return res
    }

    @Suppress("UNCHECKED_CAST")
    private fun unifyComponent(
        tree: INode<T>,
        unificator: INode<T>
    ): List<MutableMap<Placeholder<T>, INode<T>>> {
        val un = unificator.element()
        return if (un.isPlaceholder()) {
            un as Placeholder<T>
            if (un.constraint(tree)) listOf(mutableMapOf(Pair(un, tree)))
            else listOf()
        } else if (un == tree) {
            listOf(HashMap())
        } else {
            listOf()
        }
    }
}

interface Unifiable {
    /**
     * Returns true if and only if this object is equal in the sense of unification to the given [Unifiable].
     *
     * @param unifiable the other [Unifiable]
     * @return whether they are equal in the sense of unification
     */
    fun isUnifiableWith(unifiable: Unifiable): Boolean

    /**
     * Return true if node that contains this Unifiable has commutative sub-nodes, false else
     */
    fun isCommutative(): Boolean

    /**
     * Return true if node that contains this Unifiable has associative sub-nodes, false else
     */
    fun isAssociative(): Boolean

    /**
     * Returns true if this is a [Placeholder]
     */
    fun isPlaceholder(): Boolean
}