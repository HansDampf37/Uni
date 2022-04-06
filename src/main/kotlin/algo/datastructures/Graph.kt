package algo.datastructures

import algo.algorithms.Dijkstra

class Graph<T, S>(
    val nodes: MutableList<INode<T>> = mutableListOf(),
    val edges: MutableList<Edge<T, S>> = mutableListOf()
): Iterable<INode<T>> {
    class Path<T, S>(private val g: Graph<T, S>, private val nodes: List<INode<T>>): Iterable<INode<T>> {
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
        return edges.filter { it.from == node || it.to == node }.map { if (it.from == node) it.to else it.from }
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
        assert(contains(edge.from))
        assert(contains(edge.to))
        edges.add(edge)
    }
}

