package org.deg.uni.analysis.functions

import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.*
import jetbrains.letsPlot.ggplot
import jetbrains.letsPlot.ggsize
import jetbrains.letsPlot.label.ggtitle
import jetbrains.letsPlot.letsPlot
import jetbrains.letsPlot.scale.scale_fill_discrete
import org.apache.batik.anim.timing.Interval
import org.deg.uni.analysis.terms.model.*
import org.deg.uni.analysis.terms.simplifying.SimplifierGraph
import org.deg.uni.analysis.terms.x
import kotlin.math.ln

class F(private val name: String = "f", vars: List<Variable>, init: () -> Term) {
    private val vars = vars.toList()
    private val term = init()

    constructor(vararg vars: Variable, init: () -> Term) : this("f", vars.toList(), init)

    fun evaluate(startIncl: Double, endIncl: Double, density: Int): Pair<List<Double>, List<Double>> {
        assert(endIncl > startIncl)
        val xs = List(density) { i ->
            val alpha = i.toDouble() / (density - 1).toDouble()
            startIncl + alpha * (endIncl - startIncl) }
        val ys = List(density) { i -> evaluate(mapOf(Pair(vars[0], Num(xs[i])))).toDouble() }
        return Pair(xs, ys)
    }

    fun evaluate(interpretation: Map<Variable, Term>): Term {
        val old: MutableMap<Variable, Term?> = HashMap()
        for (v in interpretation) {
            old[v.key] = v.key.value
            v.key.value = v.value
        }
        val evaluation = term.clone().simplify()
        for (v in interpretation) {
            v.key.value = old[v.key]
        }
        return evaluation
    }

    fun plot(startIncl: Double, endIncl: Double, saveTo: String) {
        val (xs, ys) = evaluate(startIncl, endIncl, 60)
        println(xs)
        println(ys)
        val data = mapOf<String, Any>(
            "x" to xs,
            "y" to ys
        )
        val p = letsPlot(data)

        val layer = geomPath {
            x = "x"
            y = "y"
        }
        if (saveTo.isNotEmpty()) ggsave(p + layer, saveTo)
        p.show()
    }

    override fun toString(): String {
        return "$name(${vars.map { it.simplify() }.joinToString(", ")}) = $term"
    }

    fun cuts(f: F): List<MutableMap<Variable, Term>> {
        return Equation(term.clone(), f.term.clone()).solve()
    }

    fun derive(x: Variable): F {
        val newTerm = term.derive(x)
        return F("$name'", vars) { newTerm }
    }
}

fun main() {
    val f = F(x) {Ln(x)}
    f.plot(0.0, 100.0, "test1.png")
    f.derive(x).plot(0.0, 100.0, "test2.png")
}