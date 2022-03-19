package algo.datastructures

interface Node<T> {
    fun isLeaf() = nodeSize() == 0
    fun subNodes(): List<Node<T>> = List(nodeSize()) { getNode(it) }
    fun getNode(i: Int): Node<T>
    fun setNode(i: Int, node: Node<T>)
    fun nodeSize(): Int
    fun get(): T
}