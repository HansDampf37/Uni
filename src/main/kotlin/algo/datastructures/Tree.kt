package algo.datastructures

interface Tree<T> {
    val root: Node<T>

    fun depth() = DFS(this).depth()
    fun amountOfNodes() = DFS(this).toList().size
    fun width() = DFS(this).filter { it.isLeaf() }.size
}

class DFS<T>(val tree: Tree<T>) : Iterable<Node<T>> {
    private var depth = 0
    fun depth(): Int {
        iterator()
        return depth
    }

    override fun iterator(): Iterator<Node<T>> {
        return object : Iterator<Node<T>> {
            private val list = ArrayList<Node<T>>()
            private var i = 0

            init {
                visit(tree.root, 0)
            }

            private fun visit(node: Node<T>, currentDepth: Int) {
                if (currentDepth > depth) depth = currentDepth
                list.add(node)
                if (!node.isLeaf()) node.subNodes().forEach { visit(it, currentDepth + 1) }
            }

            override fun hasNext(): Boolean = i < list.size

            override fun next(): Node<T> = list[i++]
        }
    }
}