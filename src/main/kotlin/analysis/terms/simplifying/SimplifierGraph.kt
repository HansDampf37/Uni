package analysis.terms.simplifying

import algo.datastructures.Edge
import algo.datastructures.Graph
import algo.datastructures.INode
import algo.datastructures.ITree
import analysis.terms.Term

class SimplifierGraph : ISimplifier {
    private var cache = Cache

    override fun simplify(t: Term): Term {
        val simplified = cache.get(t)
        if (simplified != null) return simplified
        val alreadySimplified = HashSet<Term>()
        val graph: Graph<Term, Rule> = Graph()
        val start = SimplifyingNode(t)
        graph.addNode(start, listOf())
        val n = (-t.quality() * 2).toInt()
        for (i in 0 until n) {
            val current = graph.filter { !alreadySimplified.contains(it.element()) }.maxByOrNull { it.element().quality() }
                ?: break
            alreadySimplified.add(current.element())
            simplifyComponents(current.element())
            val results = simplifyWithRules(RuleBook.rules, current.element())
            for (result in results) {
                val term = result.first
                val rule = result.third
                val newNode = SimplifyingNode(term)
                if (graph.contains(newNode)) graph.addEdge(Edge(current, newNode, 1.0, rule))
                else graph.addNode(newNode, listOf(Triple(current, 1.0, rule)))
            }
        }
        val best = graph.maxByOrNull { it.element().quality() }!!
        cache.add(t, best.element())
        return best.element()
    }

    private inner class SimplifyingNode(val t: Term) : INode<Term> {
        override fun equals(other: Any?): Boolean {
            return other is SimplifyingNode && other.t == t
        }

        override fun toTree(): ITree<Term> {
            return object : ITree<Term> {
                override val root: INode<Term>
                    get() = this@SimplifyingNode
            }
        }


        override fun getNode(i: Int): INode<Term> {
            TODO("Not yet implemented")
        }

        override fun setNode(i: Int, node: INode<Term>) {
            TODO("Not yet implemented")
        }

        override fun nodeSize(): Int {
            TODO("Not yet implemented")
        }

        override fun addNode(node: INode<Term>) {
            TODO("Not yet implemented")
        }

        override fun removeNodeAt(i: Int): INode<Term> {
            TODO("Not yet implemented")
        }

        override fun clone(): SimplifyingNode {
            return SimplifyingNode(t)
        }

        override fun toString(): String = t.toString()

        override fun element() = t

        override fun hashCode(): Int {
            return t.hashCode()
        }
    }
}

