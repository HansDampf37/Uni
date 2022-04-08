package or.linearoptimization

import algebra.Vec
import analysis.terms.model.Num
import analysis.terms.model.Term

open class OptimizationProblem(private val opt: Optimize, private val constraints: MutableList<Constraint>) {
    private val tableau: SimplexTableau = SimplexTableau(opt, constraints)

    fun solve(): List<Pair<String, Term>> {
        val simplex = Simplex()
        simplex.run(tableau)
        return tableau.currentSolution
    }

    class Constraint(var parameters: Vec<Term>, var relation: Relation, var rightSide: Double)

    class Optimize(var parameters: Vec<Term>, var maxMin: MaxMin) {
        fun toMax() {
            if (maxMin == MaxMin.MIN) {
                parameters *= Num(-1)
                maxMin = MaxMin.MAX
            }
        }
    }

    override fun toString(): String {
        return "${opt.maxMin.s}\n$tableau"
    }

    enum class Relation {
        GET,
        EQ,
        LET
    }

    enum class MaxMin(val s: String) {
        MAX("Maximize"),
        MIN("Minimize")
    }
}