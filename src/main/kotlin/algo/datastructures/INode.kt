package algo.datastructures

/**
 * [Nodes][Node] that are contained in a [tree][Tree].
 *
 * @param T type-parameter of contained [object][element]
 */
interface INode<T>: Cloneable {
    /**
     * Returns true if this node has no children.
     */
    fun isLeaf() = nodeSize() == 0

    /**
     * Returns all children of this node
     */
    fun subNodes(): List<INode<T>> = List(nodeSize()) { getNode(it) }

    /**
     * Returns all children contained [elements][element]
     */
    fun children(): List<T> = List(nodeSize()) { getChild(it) }

    /**
     * Adds a new node. The newly added child should not contain this as a child or grand*child
     *
     * @param node the new node
     */
    fun addNode(node: INode<T>)

    /**
     * Returns the node with the specified index
     *
     * @param i the index
     * @return the node with the specified index
     */
    fun getNode(i: Int): INode<T>

    /**
     * Sets the node with the specified index
     *
     * @param i the index
     * @param node the new node
     */
    fun setNode(i: Int, node: INode<T>)

    /**
     * Remove the node at the specified index
     *
     * @param i the index
     * @return the removed node
     */
    fun removeNodeAt(i: Int): INode<T>

    /**
     * Returns the child with the specified index
     *
     * @param i the index
     * @return the child with the specified index
     */
    fun getChild(i: Int): T = getNode(i).element()

    /**
     * Sets the child with the specified index
     *
     * @param i the index
     * @param child the new child
     */
    fun setChild(i: Int, child :T) = setNode(i, Node(child))

    /**
     * Adds a new child.
     *
     * @param child the new child
     */
    fun addChild(child: T) = addNode(Node(child))

    /**
     * Returns the amount of children of this node
     */
    fun nodeSize(): Int

    /**
     * Returns the element contained in this [Node].
     */
    fun element(): T

    /**
     * Returns a [Tree] with this node as [Tree.root]
     */
    fun toTree(): ITree<T> = Tree(this)

    public override fun clone(): INode<T>
}

/**
 * @see INode
 */
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
