package or.algorithms.linearoptimization

import algebra.Vec
import or.linearoptimization.OptimizationProblem
import org.junit.Test

internal class OptimizationProblemTest {
    private val op = OptimizationProblem(
        OptimizationProblem.Optimize(Vec(3, 4, 2), OptimizationProblem.MaxMin.MAX),
        mutableListOf(
            OptimizationProblem.Constraint(Vec(1, 1, 1), OptimizationProblem.Relation.LET, 10.0),
            OptimizationProblem.Constraint(Vec(1, -1, 2), OptimizationProblem.Relation.LET, 4.0),
            OptimizationProblem.Constraint(Vec(0, 4, 7), OptimizationProblem.Relation.GET, 20.0),
            OptimizationProblem.Constraint(Vec(1, 1, 0), OptimizationProblem.Relation.LET, 6.8),
        )
    )

    @Test
    fun testInit() {
        println(op)
        println(op.solve())
    }
}