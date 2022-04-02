package algo.datastructures

interface INode<T>: Cloneable {
    fun isLeaf() = nodeSize() == 0
    fun subNodes(): List<INode<T>> = List(nodeSize()) { getNode(it) }
    fun children(): List<T> = List(nodeSize()) { getChild(it) }
    fun addNode(node: INode<T>)
    fun getNode(i: Int): INode<T>
    fun setNode(i: Int, node: INode<T>)
    fun removeNodeAt(i: Int): INode<T>
    fun getChild(i: Int): T = getNode(i).element()
    fun setChild(i: Int, child :T) = setNode(i, Node(child))
    fun addChild(child: T) = addNode(Node(child))
    fun nodeSize(): Int
    fun element(): T
    fun toTree(): ITree<T> = Tree(this)
    public override fun clone(): INode<T>
}

class Node<T>(private val el: T) : INode<T> {
    private val subNodes = ArrayList<INode<T>>()
    override fun getNode(i: Int): INode<T> {
        return subNodes[i]
    }

    override fun setNode(i: Int, node: INode<T>) {
        subNodes[i] = node
    }

    override fun addNode(node: INode<T>) {
        subNodes.add(node)
    }

    override fun nodeSize(): Int {
        return subNodes.size
    }

    override fun element(): T {
        return el
    }

    override fun clone(): INode<T> {
        return Node(el).apply { this@Node.subNodes.addAll(this.subNodes) }
    }

    override fun removeNodeAt(i: Int): INode<T> {
        return subNodes.removeAt(i)
    }
}
