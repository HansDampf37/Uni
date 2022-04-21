package algo.datastructures

/**
 * A Tree starting from a [rootnode][root]. The [nodes][INode] contain elements from type [T]
 *
 * @param T the type-parameter of the objects contained in the nodes
 */
interface ITree<T> {
    val root: INode<T>

    /**
     * Return the length of the longest path from the root node to any leaf.
     */
    fun depth() = DFS(this).depth()

    /**
     * Returns the amount of nodes in the tree.
     */
    fun amountOfNodes() = DFS(this).toList().size

    /**
     * Returns the amount of nodes that have no children.
     */
    fun width() = DFS(this).filter { it.isLeaf() }.size
}

/**
 * @see ITree
 */
class Tree<T>(override val root: INode<T>): ITree<T>

/**
 * Performs a depth first search on the tree.
 *
 * @param T
 * @property tree
 * @constructor Create empty D f s
 */
class DFS<T>(val tree: ITree<T>) : Iterable<INode<T>> {
    private var depth = 0
    fun depth(): Int {
        iterator()
        return depth
    }

    private val iterator = object : Iterator<INode<T>> {
        private val list = ArrayList<INode<T>>()
        private var i = 0

        init {
            visit(tree.root, 0)
        }

        private fun visit(node: INode<T>, currentDepth: Int) {
            if (currentDepth > depth) depth = currentDepth
            list.add(node)
            if (!node.isLeaf()) node.subNodes().forEach { visit(it, currentDepth + 1) }
        }

        override fun hasNext(): Boolean = i < list.size

        override fun next(): INode<T> = list[i++]
    }

    override fun iterator(): Iterator<INode<T>> {
        return iterator
    }
}