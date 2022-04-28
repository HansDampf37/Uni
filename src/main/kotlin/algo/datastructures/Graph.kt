package algo.datastructures

import Partition
import algo.algorithms.Dijkstra
import org.graphstream.graph.implementations.SingleGraph
import partitionPerm
import subsets


open class Graph<T, S>(
    nodes: List<INode<T>> = mutableListOf(),
    edges: List<Edge<T, S>> = mutableListOf()
) : Iterable<INode<T>> {

    protected val nodes: MutableList<INode<T>> = nodes.toMutableList()
    protected val edges: MutableList<Edge<T, S>> = ArrayList()
    init {
        nodes.forEach { for (i in 0 until it.nodeSize()) it.removeNodeAt(0) }
        edges.forEach { addEdge(it) }
    }

    class Path<T, S>(private val g: Graph<T, S>, nodes: List<INode<T>>) : Iterable<INode<T>>,
        Graph<T, S>(nodes, List(nodes.size - 1) { i ->
            g.edges.first { it.from == nodes[i] && it.to == nodes[i + 1] || it.to == nodes[i] && it.from == nodes[i + 1] }
        }) {

        fun dist(): Double {
            var sum = 0.0
            for (i in 0 until nodes.size - 1) {
                sum += g.weightOfEdge(nodes[i], nodes[i + 1])
            }
            return sum
        }

        override fun toString(): String {
            return nodes.map { it.element() }.toString()
        }

        override fun iterator(): Iterator<INode<T>> = nodes.iterator()
    }

    fun addNode(node: INode<T>, neighbors: List<Triple<INode<T>, Double, S?>>) {
        assert(!nodes.contains(node))
        nodes.add(node)
        neighbors.forEach { n -> edges.add(Edge(n.first, node, n.second, n.third)) }
        neighbors.forEach {
            it.first.addNode(node)
            node.addNode(it.first)
        }
    }

    fun getNeighborsOf(node: INode<T>): List<INode<T>> {
        return edges.filter { it.from == node }.map { it.to } + edges.filter { it.to == node }.map { it.from }
    }

    val v get() = nodes.toList()
    val e get() = edges.toList()

    fun dist(u: INode<T>, v: INode<T>): Double {
        return shortestPath(u, v).dist()
    }

    fun shortestPath(u: INode<T>, v: INode<T>): Path<T, S> {
        val dijkstra = Dijkstra(this)
        return dijkstra.createShortestPathTo(v, dijkstra.run(u))
    }

    fun weightOfEdge(u: INode<T>, v: INode<T>): Double {
        val e = e.firstOrNull { it.from == u && it.to == v || it.from == v && it.to == u }
            ?: throw IllegalArgumentException("u and v are not neighbors")
        return e.weight
    }

    override fun iterator(): Iterator<INode<T>> {
        return nodes.iterator()
    }

    fun addEdge(edge: Edge<T, S>) {
        assert(v.contains(edge.from))
        assert(v.contains(edge.to))
        edges.add(edge)
        edge.to.addNode(edge.from)
        edge.from.addNode(edge.to)
    }

    fun inducedSubGraph(nodes: Iterable<INode<T>>): Graph<T, S> {
        return Graph(v.filter { nodes.contains(it) }, e.filter { nodes.contains(it.from) && nodes.contains(it.to) })
    }

    fun isClique(): Boolean {
        for (node in v) {
            val neighbors = getNeighborsOf(node)
            for (neighbor in v) {
                if (neighbor == node) continue
                if (!neighbors.contains(neighbor)) return false
            }
        }
        return true
    }

    fun isIndependent(): Boolean {
        return e.isEmpty()
    }

    fun omega(): Int {
        return biggestClique().size
    }

    fun alpha(): Int {
        return biggestIndependent().size
    }

    fun chi(): Int {
        return v.partitionPerm().filter { isColorization(it) }.minByOrNull { it.size }!!.size
    }

    fun kappa(): Int {
        return v.partitionPerm().filter { isCliqueOverlap(it) }.minByOrNull { it.size }!!.size
    }

    fun isColorization(partitions: Partition<INode<T>>): Boolean {
        return partitions.all { inducedSubGraph(it).isIndependent() }
    }

    fun isCliqueOverlap(partitions: Partition<INode<T>>): Boolean {
        return partitions.all { inducedSubGraph(it).isClique() }
    }

    fun biggestClique(): List<INode<T>> {
        if (v.isEmpty()) return listOf()
        return v.subsets().map { inducedSubGraph(it) }.filter { it.isClique() }.maxByOrNull { it.v.size }!!.v
    }

    fun biggestIndependent(): List<INode<T>> {
        if (v.isEmpty()) return listOf()
        return v.subsets().map { inducedSubGraph(it) }.filter { it.isIndependent() }
            .maxByOrNull { it.v.size }!!.v
    }

    fun complement(): Graph<T, S> {
        val edges = ArrayList<Edge<T, S>>()
        for (v1 in v) {
            for (v2 in v) {
                if (v1 == v2) continue
                if (v1.subNodes().contains(v2) || v2.subNodes().contains(v1)) continue
                edges.add(Edge(v1, v2))
            }
        }
        return Graph(v, edges)
    }

    operator fun times(node: INode<T>): Graph<T, S> {
        assert(v.contains(node))
        val nodeCp = Node(node.element())
        node.subNodes().forEach { nodeCp.addNode(it) }
        val vCp = v.toMutableList()
        vCp.add(nodeCp)
        val eCp = e.toMutableList()
        eCp.filter { it.from == node }.forEach { eCp.add(Edge(nodeCp, it.to, it.weight, it.el)) }
        eCp.filter { it.to == node }.forEach { eCp.add(Edge(it.to, nodeCp, it.weight, it.el)) }
        return Graph(vCp, eCp)
    }

    operator fun times(list: List<Int>): Graph<T, S> {
        assert(list.size == v.size)
        var g = this
        for (i in v.indices) {
            for (amount in 0 until list[i]) {
                g *= v[i]
            }
        }
        return g
    }

    operator fun minus(node: INode<T>): Graph<T, S> {
        assert(v.contains(node))
        return inducedSubGraph(v.toMutableList().apply { remove(node) })
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Graph<*, *>) return false
        val v = v.toSet()
        val vOther = other.v.toSet()
        val e = e.toSet()
        val eOther = other.e.toSet()
        return v == vOther && e == eOther
    }

    override fun hashCode(): Int {
        var result = nodes.hashCode()
        result = 31 * result + edges.hashCode()
        return result
    }

    fun isPerfect(): Boolean {
        return v.subsets().all { inducedSubGraph(it).alpha() * inducedSubGraph(it).omega() >= it.size }
    }

    companion object {
        fun <T, S> kn(
            n: Int,
            initNode: (Int) -> T?,
        ): Graph<T?, S> {
            val nodes = List(n) { Node(initNode(it)) }
            val edges = ArrayList<Edge<T?, S>>()
            for (node1 in nodes.indices) {
                for (node2 in node1 until nodes.size) {
                    if (node1 != node2) edges.add(Edge(nodes[node1], nodes[node2], 0.0, null))
                }
            }
            return Graph(nodes, edges)
        }

        fun kn(n: Int): Graph<Any?, Any> {
            return kn(n) { null }
        }

        fun <T, S> knm(
            n: Int,
            m: Int,
            initNodeN: (Int) -> T?,
            initNodeM: (Int) -> T?,
            initWeight: (Int) -> Double,
            initEdgeEl: (Int) -> S?
        ): Graph<T?, S> {
            val nodesN = List(n) { Node(initNodeN(it)) }
            val nodesM = List(m) { Node(initNodeM(it)) }
            val edges = List(n * m) { i ->
                val fromIndex = i / m
                val toIndex = i % m
                Edge(nodesN[fromIndex], nodesM[toIndex], initWeight(i), initEdgeEl(i))
            }
            val nodes: List<INode<T?>> = nodesN + nodesM
            return Graph(nodes, edges)
        }

        fun knm(n: Int, m: Int): Graph<Any?, Any> {
            return knm(n, m, { null }, { null }, { 1.0 }, { null })
        }

        fun cn(n: Int): Graph<Any?, Any> {
            if (n == 0) return Graph()
            return pn(n).apply { addEdge(Edge(v.last(), v.first())) }
        }

        fun pn(n: Int): Graph<Any?, Any> {
            if (n == 0) return Graph()
            val nodes: List<Node<Any?>> = List(n) { Node(null) }
            return Graph(nodes, edges = List(n - 1) { i -> Edge(nodes[i], nodes[i + 1], 0.0, { null }) })
        }

        fun en(n: Int): Graph<Any?, Any> {
            return Graph(List(n) { Node(null) }, listOf())
        }
    }

    fun display() {
        System.setProperty("org.graphstream.ui", "swing")
        val g = SingleGraph("test")
        var i = 0
        val nodeNames = HashMap<INode<T>, String>()
        v.forEach { nodeNames[it] = if (it.element() == null) i++.toString() else it.element().toString() }
        v.forEach { g.addNode(nodeNames[it]) }
        e.forEach {
            val content = if (it.el == null) {
                "${nodeNames[it.to]} - ${nodeNames[it.from]}"
            } else {
                it.el.toString()
            }
            g.addEdge(content, nodeNames[it.from], nodeNames[it.to])
        }
        g.display()
    }
}

fun main() {
    System.setProperty("org.graphstream.ui", "swing")
    Graph.knm(100,50).display()
}

