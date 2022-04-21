package algo.datastructures

import Partition
import algo.algorithms.Dijkstra
import partition
import permute

class Graph<T, S>(
    nodes: List<INode<T>> = mutableListOf(),
    edges: List<Edge<T, S>> = mutableListOf()
) : Iterable<INode<T>> {

    private val nodes: MutableList<INode<T>> = nodes.toMutableList()
    private val edges: MutableList<Edge<T, S>> = edges.toMutableList()

    class Path<T, S>(private val g: Graph<T, S>, private val nodes: List<INode<T>>) : Iterable<INode<T>> {
        fun edges(): List<Edge<T, S>> {
            return List(nodes.size - 1) { i ->
                g.edges.first { it.from == g.edges[i] && it.to == g.edges[i + 1] || it.to == g.edges[i] && it.from == g.edges[i + 1] }
            }
        }

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
        return listOfNodePartitions().filter { isColorization(it) }.minByOrNull { it.size }!!.size
    }

    fun kappa(): Int {
        return listOfNodePartitions().filter { isCliqueOverlap(it) }.minByOrNull { it.size }!!.size
    }

    fun isColorization(partitions: Partition<INode<T>>): Boolean {
        return partitions.all { inducedSubGraph(it).isIndependent() }
    }

    fun isCliqueOverlap(partitions: Partition<INode<T>>): Boolean {
        return partitions.all { inducedSubGraph(it).isClique() }
    }

    fun biggestClique(): List<INode<T>> {
        return listOfNodeSubsets().map { inducedSubGraph(it) }.filter { it.isClique() }.maxByOrNull { it.v.size }!!.v
    }

    fun biggestIndependent(): List<INode<T>> {
        return listOfNodeSubsets().map { inducedSubGraph(it) }.filter { it.isIndependent() }
            .maxByOrNull { it.v.size }!!.v
    }

    fun listOfNodeSubsets(): List<List<INode<T>>> {
        return v.permute().map { it.partition(2) }.flatten().map { it.first() }.toMutableList()
            .apply { add(v.toList()) }
    }

    fun listOfNodePartitions(): List<Partition<INode<T>>> {
        val permuted = v.permute()
        return IntRange(1, v.size).map { n -> permuted.map { it.partition(n)}.flatten() }.flatten()
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
            return pn(n).apply { addEdge(Edge(v.last(), v.first())) }
        }

        fun pn(n: Int): Graph<Any?, Any> {
            val nodes: List<Node<Any?>> = List(n) { Node(null) }
            return Graph(nodes, edges = List(n - 1) { i -> Edge(nodes[i], nodes[i + 1], 0.0, { null }) })
        }

        fun en(n: Int): Graph<Any?, Any> {
            return Graph(List(n) { Node(null) }, listOf())
        }
    }
}

