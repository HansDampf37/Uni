package numerik

import algebra.Matrix
import algebra.Vec
import algebra.len
import analysis.terms.Num
import analysis.terms.Term
import kotlin.math.sign

class QR : Decomposition {
    private fun w(v: Vec<Term>): Vec<Term> {
        val sigma = if (v[0].toDouble() > 0) Num(0) - v.len() else v.len()
        val e1 = Vec(List(v.size) { i -> if (i == 0) Num(1) else Num(0) })
        val res = v - e1 * sigma
        return res / res.len()
    }

    fun decomp(a: Matrix<Term>): Pair<Matrix<Term>, Vec<Term>> {
        val a1 = a.col(0)
        val w = w(a1)
        val houseHolderTransformed = qTimesA(w, a)
        val r11 = Num(-a1[0].toDouble().sign) * a1.len()
        if (houseHolderTransformed.width == 1) {
            return Pair(Matrix(w.map { Vec(it) }), Vec(r11))
        }
        val r12 = houseHolderTransformed.subMatrix(0, 1, 1, houseHolderTransformed.width)
        val newProblem = houseHolderTransformed.subMatrix(1, houseHolderTransformed.height, 1, houseHolderTransformed.width)
        val solution = decomp(newProblem)
        val solutionMatrix = solution.first
        val composed = w.toMatrix().extendCols(r12.extendRows(solutionMatrix))
        return Pair(composed, solution.second.apply{add(0, r11)})
    }

    private fun qTimesA(w: Vec<Term>, a: Matrix<Term>): Matrix<Term> {
        val nextA = a.zero().transpose()
        for (i in 0 until nextA.height) {
            nextA[i] = a.col(i) - w * (w * a.col(i)) * Num(2)
        }
        return nextA.transpose()
    }
}

fun Matrix<Term>.qrDecomp(): Pair<Matrix<Term>, Vec<Term>> = QR().decomp(this)