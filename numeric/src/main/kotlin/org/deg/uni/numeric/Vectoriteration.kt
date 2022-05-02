package org.deg.uni.numeric

import org.deg.uni.algebra.Matrix
import org.deg.uni.algebra.Vec
import org.deg.uni.algebra.len
import org.deg.uni.analysis.terms.model.Num
import org.deg.uni.analysis.terms.model.Term

class Vectoriteration {
    fun run(m: Matrix<Term>): Pair<Vec<Term>, Term> {
        var cur = Vec(m.width) { Num(1) }
        repeat(10) {
            cur = m * cur
            cur /= cur.len()
        }
        val ew = cur * (m * cur) / cur.len()
        return Pair(cur, ew)
    }
}