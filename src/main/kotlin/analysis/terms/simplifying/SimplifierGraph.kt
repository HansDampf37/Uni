package analysis.terms.simplifying

import algo.datastructures.*
import analysis.terms.model.Term
import propa.IRule

class SimplifierGraph : ISimplifier {
    private var cache = Cache

    override fun simplify(t: Term): Term {
        val simplified = cache.get(t)
        if (simplified != null) return simplified
        val alreadySimplified = HashSet<Term>()
        val graph: Graph<Term, IRule<INode<Term>, INode<Term>>> = Graph()
        val start = SimplifyingNode(t)
        graph.addNode(start, listOf())
        val n = (-t.quality() * 2).toInt()
        var i = 0
        var continueToImprove = false
        var best = graph.maxByOrNull { it.element().quality() }!!
        while (i < n || continueToImprove) {
            if (i == 41) {
                println("")
            }
            val current = graph.filter { !alreadySimplified.contains(it.element()) }.maxByOrNull { it.element().quality() }
                ?: break
            alreadySimplified.add(current.element())
            val results = simplifyWithRules(RuleBook.rules, current.element())
            for (result in results) {
                val term = result.first
                val rule = result.third
                val newNode = SimplifyingNode(term)
                if (graph.contains(newNode)) graph.addEdge(Edge(current, newNode, 1.0, rule))
                else graph.addNode(newNode, listOf(Triple(current, 1.0, rule)))
            }
            i++
            val newBest = graph.maxByOrNull { it.element().quality() }!!
            if (newBest.element().quality() > best.element().quality()) {
                continueToImprove = true
                best = newBest
            } else {
                continueToImprove = false
            }

        }
        cache.add(t, best.element())
        graph.display()
        return best.element()
    }

    private inner class SimplifyingNode(t: Term) : Node<Term>(t) {
        override fun equals(other: Any?): Boolean {
            return other is SimplifyingNode && other.element() == element()
        }

        override fun toTree(): ITree<Term> {
            return object : ITree<Term> {
                override val root: INode<Term>
                    get() = this@SimplifyingNode
            }
        }


        override fun clone(): SimplifyingNode {
            return SimplifyingNode(element())
        }

        override fun toString(): String = element().toString()

        override fun hashCode(): Int {
            return element().hashCode()
        }
    }
}

