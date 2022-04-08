package or.algorithms

import algebra.Vec
import analysis.terms.model.Num
import analysis.terms.model.Product
import analysis.terms.model.Term
import analysis.terms.model.Variable
import or.linearoptimization.OptimizationProblem
import or.linearoptimization.Simplex
import or.linearoptimization.SimplexTableau
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.roundToInt

internal class SimplexTest {
    private val tableau = SimplexTableau(
        OptimizationProblem.Optimize(Vec(4, 3), OptimizationProblem.MaxMin.MAX),
        mutableListOf(
            OptimizationProblem.Constraint(Vec(0, 1), OptimizationProblem.Relation.LET, 6.0),
            OptimizationProblem.Constraint(Vec(1, 1), OptimizationProblem.Relation.LET, 7.0),
            OptimizationProblem.Constraint(Vec(3, 2), OptimizationProblem.Relation.LET, 18.0),
        )
    )

    @Test
    fun getBaseVariables() {
        assertEquals(listOf("s1", "s2", "s3"), tableau.baseVariables)
    }

    @Test
    fun getNonBaseVariables() {
        assertEquals(listOf("x1", "x2"), tableau.nonBaseVariables)
    }

    @Test
    fun getZ() {
        assertEquals(0.0, tableau.z)
    }

    @Test
    fun getCurrentSolution() {
        println(tableau.currentSolution)
    }

    @Test
    fun run() {
        val s = Simplex()
        s.run(tableau)
        assertEquals(listOf(Pair("s1", 3.0), Pair("x2", 3.0), Pair("x1", 4.0)), tableau.currentSolution.map { Pair(it.first, (it.second.toDouble() * 100).roundToInt() / 100.0)})
    }
}