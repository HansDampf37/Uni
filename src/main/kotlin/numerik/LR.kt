package numerik

import algebra.Matrix
import algebra.Vec
import analysis.Field
import analysis.terms.Num
import analysis.terms.Term
import analysis.abs

class LR : Decomposition {
    fun <T> decomp(m: Matrix<T>): Triple<Matrix<T>, Matrix<T>, Vec<Term>> where T : Field<T>, T: Comparable<T> {
        if (m.width != m.height) throw IllegalArgumentException("LR decomposition only works on regular matrices")
        val r = m.clone()
        val l = m.one()
        val p = Vec(List(m.height) { Num(it) } )
        for (pivot in 0 until r.height) {
            val maxIndex = r.withIndex().filter { it.index >= pivot }.maxByOrNull { it.value[pivot].abs() }!!.index
            r.swapLines(pivot, maxIndex)
            val temp = p[pivot]
            p[pivot] = p[maxIndex]
            p[maxIndex] = temp
            for (underRow in pivot + 1 until r.height) {
                val scalar = r[underRow][pivot] / r[pivot][pivot]
                r.addRowToRow(pivot, r[pivot][pivot].zero() - scalar, underRow)
                l[underRow][pivot] = scalar
            }
        }
        return Triple(l, r, p)
    }

    fun <T> run(m: Matrix<T>, b: Vec<T>): Vec<T> where T : Field<T>, T: Comparable<T> {
        val (l, r, p) = decomp(m)
        val y = forwardSub(l, b, p)
        return backwardSub(r, y)
    }
}

fun <T> Matrix<T>.lrDecomp(): Triple<Matrix<T>, Matrix<T>, Vec<Term>> where T : Field<T>, T: Comparable<T> {
    return LR().decomp(this)
}

fun <T> Matrix<T>.solveLGSwithLR(b: Vec<T>): Vec<T> where T : Field<T>, T: Comparable<T> {
    return LR().run(this, b)
}
