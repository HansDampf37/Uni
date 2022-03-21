package algo.algorithms

import algo.datastructures.Graph
import algo.datastructures.Node

class Dijkstra<T, S>(private val graph: Graph<T, S>) {
    private val dist: HashMap<Node<T>, Double> = HashMap()
    private val pred: HashMap<Node<T>, Node<T>> = HashMap()
    private val q = graph.v.toMutableList()

    private fun initialize(start: Node<T>) {
        graph.v.forEach { n -> dist[n] = Double.MAX_VALUE }
        dist[start] = 0.0
    }

    fun run(start: Node<T>): HashMap<Node<T>, Node<T>> {
        assert(graph.contains(start))
        initialize(start)
        while (q.isNotEmpty()) {
            // u = node in q with the smallest value in distance
            val indexOfMin: Int = q.withIndex().minByOrNull { el: IndexedValue<Node<T>> -> dist[el.value]!! }!!.index
            val u = q[indexOfMin]
            q.remove(u)
            graph.getNeighborsOf(u).forEach { v ->
                if (q.contains(v)) {
                    updateDist(u, v)
                }
            }
        }
        return pred
    }

    private fun updateDist(u: Node<T>, v: Node<T>) {
        val alternative =
            dist[u]!! + graph.weightOfEdge(u, v)
        if (alternative < dist[v]!!) {
            dist[v] = alternative
            pred[v] = u
        }
    }

    fun createShortestPathTo(node: Node<T>, pred: HashMap<Node<T>, Node<T>>): Graph.Path<T, S> {
        var node1 = node
        val path = mutableListOf(node1)
        while (pred[node1] != null) {
            path.add(0, pred[node1]!!)
            node1 = pred[node1]!!
        }
        return Graph.Path(graph, path)
    }
}