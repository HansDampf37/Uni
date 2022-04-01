package algo.datastructures

interface INode<T> {
    fun isLeaf() = nodeSize() == 0
    fun subNodes(): List<INode<T>> = List(nodeSize()) { getNode(it) }
    fun getNode(i: Int): INode<T>
    fun setNode(i: Int, node: INode<T>)
    fun nodeSize(): Int
    fun get(): T
    fun toTree(): ITree<T>
}

class Node<T>(private val el: T) : INode<T> {
    val list = ArrayList<INode<T>>()
    override fun getNode(i: Int): INode<T> {
        return list[i]
    }

    override fun setNode(i: Int, node: INode<T>) {
        list[i] = node
    }

    override fun nodeSize(): Int {
        return list.size
    }

    override fun get(): T {
        return el
    }

    override fun toTree(): ITree<T> {
        TODO()
    }
}
