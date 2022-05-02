package org.deg.uni.numeric

import org.deg.uni.algebra.Matrix
import org.deg.uni.algebra.Vec
import org.deg.uni.algebra.len
import org.deg.uni.analysis.terms.model.Num
import org.deg.uni.analysis.terms.model.Term
import org.deg.uni.analysis.unaryMinus

class QR : Decomposition {
    private fun w(v: Vec<Term>): Vec<Term> {
        val sigma = if (v[0].toDouble() > 0) -v.len() else v.len()
        val e1 = Vec(List(v.size) { i -> if (i == 0) Num(1) else Num(0) })
        val res = v - e1 * sigma
        return res / res.len()
    }

    fun decomp(a: Matrix<Term>): Matrix<Term> {
        if (a.height < a.width) throw IllegalArgumentException()
        val w = w(a.col(0))
        val houseHolderTransformed = qTimesA(w, a)
        if (a.width == 1) return houseHolderTransformed
        val r11 = houseHolderTransformed[0][0]
        val a12 = houseHolderTransformed[0].cut(1, houseHolderTransformed.width)
        val aNew =
            houseHolderTransformed.subMatrix(1, houseHolderTransformed.height, 1, houseHolderTransformed.width)
        val qrNew = decomp(aNew)
        val firstColumnOfResult = Vec(r11).apply { addAll(w.cut(1, w.size)) }
        val rightBlock = Matrix(a12).extendRows(qrNew)
        return firstColumnOfResult.toMatrix().extendCols(rightBlock)
    }

    private fun qTimesA(w: Vec<Term>, a: Matrix<Term>): Matrix<Term> {
        return Matrix(List(a.width) { i -> qTimesA(w, a.col(i)) }).transpose()
    }

    private fun qTimesA(w: Vec<Term>, a: Vec<Term>): Vec<Term> {
        return a - w * (a * w * Num(2))
    }

    fun getQR(qrDecomp: Matrix<Term>): Pair<Matrix<Term>, Matrix<Term>> {
        val r = Matrix(qrDecomp.width, qrDecomp.height) { i: Int, j: Int ->
            if (i <= j) qrDecomp[i][j] else Num(0)
        }
        return Pair(r, r)
    }
}

fun Matrix<Term>.qrDecomp(): Matrix<Term> = QR().decomp(this)