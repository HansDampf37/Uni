package numerik

import algebra.Matrix
import algebra.Vec
import algebra.len
import analysis.terms.Num
import analysis.terms.Term

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