package or.linearoptimization

import algebra.Matrix
import algebra.Vec
import analysis.terms.model.Term

open class Simplex {
    fun run(t: SimplexTableau): SimplexTableau {
        while (true) {
            println(t)
            val column = t.m[0].withIndex().minByOrNull { v: IndexedValue<Term> -> v.value.toDouble() }!!.index
            if (t.m[0][column].toDouble() >= 0) return t
            val row = t.m.withIndex().filter { it.value[column].toDouble() > 0.0 }
                .minByOrNull { v: IndexedValue<Vec<Term>> -> v.value.last().toDouble() / v.value[column].toDouble() }?.index
                ?: return t
            val pivot = t.m[row][column]
            println("Pivot: ($row, $column) = $pivot")
            val swap1 = t.nonBaseVariables[column]
            val swap2 = t.baseVariables[row - 1]
            t.nonBaseVariables[column] = swap2
            t.baseVariables[row - 1] = swap1
            val mNew = Matrix<Term>(t.m.width, t.m.height) { i, j ->
                if (i == row && j == column) {
                    t.m[i][j].one()
                } else if (j == column) {
                    t.m[i][j].zero()
                } else if (i == row) {
                    t.m[i][j] / pivot
                } else {
                    t.m[i][j] - t.m[i][column] / pivot * t.m[row][j]
                }
            }
            t.m = mNew
        }
    }
}