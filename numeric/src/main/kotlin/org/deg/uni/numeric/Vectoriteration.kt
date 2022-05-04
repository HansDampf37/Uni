package org.deg.uni.numeric

import org.deg.uni.algebra.*
import org.deg.uni.analysis.terms.model.Num
import org.deg.uni.analysis.terms.model.Term

class Vectoriteration {
    fun run(m: SimpleMatrix): Pair<SimpleVec, Double> {
        var cur = SimpleVec(m.width) { 1.0 }
        repeat(10) {
            cur = m * cur
            cur /= cur.len()
        }
        val ew = cur * (m * cur) / cur.len()
        return Pair(cur, ew)
    }
}