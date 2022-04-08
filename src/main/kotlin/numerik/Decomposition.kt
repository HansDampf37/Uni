package numerik

import algebra.Matrix
import algebra.Vec
import analysis.Field
import analysis.terms.model.Num
import analysis.terms.model.Term

interface Decomposition {
    fun <T : Field<T>> forwardSub(l: Matrix<T>, b: Vec<T>, p: Vec<Term> = Vec(List(b.size) { Num(it) })): Vec<T> {
        val b1 = Vec(List(b.size) { i -> b[p[i].toInt()] })
        val y = Vec(List(b1.size) { l[0][0].zero() })
        for (i in (0 until y.size)) {
            if (l[i][i] == Num(0)) throw ImpossibleSubstitutionException("Cant perform forward sub on l = \n$l")
            y[i] = (b1[i] - l[i] * y) / l[i][i]
        }
        return y
    }

    fun <T : Field<T>> backwardSub(r: Matrix<T>, y: Vec<T>): Vec<T> {
        val x = Vec(List(y.size) { r[0][0].zero() })
        for (i in (0 until y.size).reversed()) {
            if (r[i][i] == Num(0)) throw ImpossibleSubstitutionException("Cant perform forward sub on lr= \n$r")
            x[i] = (y[i] - r[i] * x) / r[i][i]
        }
        return x
    }
}

class ImpossibleSubstitutionException(s: String): Exception(s)