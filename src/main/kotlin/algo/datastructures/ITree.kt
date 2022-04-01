package algo.datastructures

interface ITree<T> {
    val root: INode<T>

    fun depth() = DFS(this).depth()
    fun amountOfNodes() = DFS(this).toList().size
    fun width() = DFS(this).filter { it.isLeaf() }.size
}

class Tree<T>(override val root: INode<T>): ITree<T>

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