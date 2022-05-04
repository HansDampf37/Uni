package org.deg.uni.numeric

import org.deg.uni.algebra.Matrix
import org.deg.uni.algebra.Vec
import org.deg.uni.analysis.terms.model.Term
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LRTest {

    @Test
    fun decomp() {
        val m = Matrix<Term>(Vec(1, 4, -1), Vec(3, 0, 5), Vec(2, 2, 1))
        val (l, r, p) = m.lrDecomp()
        println(l)
        println(r)
        assertEquals(Matrix(List(m.height) { i -> m[p[i].toInt()] }), l * r)
    }

    @Test
    fun run() {
        val m = Matrix<Term>(Vec(1, 4, -1), Vec(3, 0, 5), Vec(2, 2, 1))
        val b = Vec<Term>(1, 0, 0)
        val x = m.solveLGSwithLR(b)
        println("m = $m")
        println("x = $x")
        println("b = $b")
        assertEquals(b, m * x)
    }
}