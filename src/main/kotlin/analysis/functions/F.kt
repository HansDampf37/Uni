package analysis.functions

import analysis.terms.Term
import analysis.terms.Variable
import analysis.terms.simplify

class F(private val name: String = "f", vars: List<Variable>, init: () -> Term) {
    private val vars = vars.toList()
    private val term = init().simplify()

    constructor(vararg vars: Variable, init: () -> Term): this("f", vars.toList(), init)

    fun evaluate(interpretation: Map<Variable, Term>): Term {
        val old: MutableMap<Variable, Term?> = HashMap()
        for (v in vars) {
            old[v] = v.value
            v.value = interpretation[v]
        }
        val evaluation = term.clone().simplify()
        for (v in vars) {
            v.value = old[v]
        }
        return evaluation
    }

    override fun toString(): String {
        return "$name(${vars.map { it.simplify() }.joinToString(", ")}) = ${term.simplify()}"
    }

    fun cuts(f: F): List<MutableMap<Variable, Term>> {
        return Equation(term.clone(), f.term.clone()).solve()
    }

    fun derive(x: Variable): F {
        val newTerm = term.derive(x)
        return F("$name'", vars) { newTerm }
    }
}