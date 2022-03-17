package numerik

import algebra.Matrix
import algebra.Vec
import analysis.terms.Num
import analysis.terms.Term

class Cholesky : Decomposition {
    fun decomp(m: Matrix<Term>): Matrix<Term> {
        val n = m.height
        val l = m.zero()
        l[0][0] = m[0][0].sqrt()
        for (i in 1 until n) {
            val y = try {
                forwardSub(l.subMatrix(0, i, 0, i), m[i].cut(0, i))
            } catch(e: ImpossibleSubstitutionException) {
                e.printStackTrace()
                throw NoDecompositionException("$m is not symmetrical or not positive definite")
            }
            l[i] = y.extend(l.width) { Num(0) }
            val num = m[i][i] - y * y
            if (num.toDouble() >= 0) {
                l[i][i] = num.sqrt()
            } else {
                throw NoDecompositionException("$m is not symmetrical or not positive definite")
            }
        }
        return l
    }

    fun run(m: Matrix<Term>, b: Vec<Term>): Vec<Term> {
        val l = decomp(m)
        try {
            val y = forwardSub(l, b)
            return backwardSub(l.transpose(), y)
        } catch (e: ImpossibleSubstitutionException) {
            e.stackTrace
            throw NoDecompositionException("$m is not symmetrical or not positive definite")
        }
    }
}

class NoDecompositionException(s: String): Exception(s)

fun Matrix<Term>.choleskyDecomp(): Matrix<Term> {
    return Cholesky().decomp(this)
}

fun Matrix<Term>.solveLGSwithCholesky(b: Vec<Term>): Vec<Term> {
    return Cholesky().run(this, b)
}
