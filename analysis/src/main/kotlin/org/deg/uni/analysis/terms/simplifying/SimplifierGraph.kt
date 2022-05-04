package org.deg.uni.analysis.terms.simplifying

import org.deg.uni.analysis.terms.model.Term
import org.deg.uni.graphs.datastructures.*
import org.deg.uni.unification.IRule
import java.lang.Thread.sleep


class SimplifierGraph : ISimplifier {
    private var cache = Cache
    var showInGui: Boolean = false
    lateinit var graph: Graph<Term, IRule<INode<Term>, INode<Term>>>

    override fun simplify(t: Term): Term {
       /* val simplified = cache.get(t)
        if (simplified != null) return simplified*/
        val alreadySimplified = HashSet<Term>()
        val graph: Graph<Term, IRule<INode<Term>, INode<Term>>> = Graph()
        if (showInGui) graph.display()
        val start = SimplifyingNode(t)
        graph.addNode(start, listOf())
        val n = (-t.quality() * 2).toInt()
        var i = 0
        var continueToImprove = false
        var best = graph.maxByOrNull { it.element().quality() }!!
        while (i < n || continueToImprove) {
            val current = graph.filter { !alreadySimplified.contains(it.element()) }.maxByOrNull { it.element().quality() }
                ?: break
            alreadySimplified.add(current.element())
            val results = simplifyWithRules(RuleBook.rules, current.element())
            for (result in results) {
                val term = result.first
                val rule = result.third
                val newNode = SimplifyingNode(term)
                if (graph.none { node -> node.element() === term }) graph.addNode(newNode, listOf(Triple(current, 1.0, rule)))
                else graph.addEdge(Edge(current, graph.first {it.element() == term}, 1.0, rule))
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
        if (showInGui) {
            graph.graphViewer.setAttributeForNode(best, "ui.class", "best")
            graph.graphViewer.showShortestPathFrom(start, best)
            sleep(2000000)
        }
        this.graph = graph
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

