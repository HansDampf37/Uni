package org.deg.uni.numeric

import org.deg.uni.algebra.SimpleMatrix
import org.deg.uni.algebra.SimpleVec
import org.deg.uni.algebra.len
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

internal class VectoriterationTest {
    @Test
    fun run() {
        val result = Vectoriteration().run(SimpleMatrix(SimpleVec(1, 3, 0), SimpleVec(0, 4, 0), SimpleVec(1, 0, -1)))
        println(result)
        var eigenvector = SimpleVec(5, 5, 1)
        eigenvector /= eigenvector.len()
        val sp1 = result.first * eigenvector
        val sp2 = eigenvector * eigenvector
        assertTrue(abs(sp1 / sp2 - 1)  < 0.01)
        assertTrue(abs(result.second / 4 - 1) < 0.01)
    }
}