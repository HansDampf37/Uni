package algo.datastructures

import algo.algorithms.Dijkstra

class Graph<T, S>(
    private val nodes: MutableList<Node<T>> = mutableListOf(),
    private val edges: MutableList<Edge<T, S>> = mutableListOf()
): Iterable<Node<T>> {
    class Path<T, S>(private val g: Graph<T, S>, private val nodes: List<Node<T>>): Iterable<Node<T>> {
        fun dist(): Double {
            var sum = 0.0
            for (i in 0 until nodes.size - 1) {
                sum += g.weightOfEdge(nodes[i], nodes[i + 1])
            }
            return sum
        }

        override fun toString(): String {
            return nodes.map { it.get() }.toString()
        }

        override fun iterator(): Iterator<Node<T>> = nodes.iterator()
    }

    fun addNode(node: Node<T>, neighbors: List<Triple<Node<T>, Double, S?>>) {
        assert(!nodes.contains(node))
        nodes.add(node)
        neighbors.forEach { n -> edges.add(Edge(node, n.first, n.second, n.third)) }
    }

    fun getNeighborsOf(node: Node<T>): List<Node<T>> {
        return edges.filter { it.from == node || it.to == node }.map { if (it.from == node) it.to else it.from }
    }

    val v get() = nodes.toList()
    val e get() = edges.toList()

    fun dist(u: Node<T>, v: Node<T>): Double {
        return shortestPath(u, v).dist()
    }

    fun shortestPath(u: Node<T>, v: Node<T>): Path<T, S> {
        val dijkstra = Dijkstra(this)
        return dijkstra.createShortestPathTo(v, dijkstra.run(u))
    }

    fun weightOfEdge(u: Node<T>, v: Node<T>): Double {
        val e = e.firstOrNull { it.from == u && it.to == v || it.from == v && it.to == u }
            ?: throw IllegalArgumentException("u and v are not neighbors")
        return e.weight
    }

    override fun iterator(): Iterator<Node<T>> {
        return nodes.iterator()
    }

    fun addEdge(edge: Edge<T, S>) {
        assert(contains(edge.from))
        assert(contains(edge.to))
        edges.add(edge)
    }
}

