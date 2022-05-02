package numerik

import algebra.Matrix
import algebra.Vec
import analysis.terms.model.Term
import org.deg.uni.numeric.NoDecompositionException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

internal class CholeskyTest {

    @Test
    fun decomp() {
        val m = Matrix<Term>(Vec(1, 2), Vec(2, 5))
        val l = m.choleskyDecomp()
        println(l)
        assertEquals(m, l * l.transpose())
    }

    @Test
    fun run() {
        val m = Matrix<Term>(Vec(1, 2), Vec(2, 5))
        val b = Vec<Term>(1, 0)
        val x = m.solveLGSwithCholesky(b)
        assertEquals(b, m * x)
    }

    @Test
    fun notSpd() {
        val m = Matrix<Term>(Vec(1, -3), Vec(-3, 5))
        val b = Vec<Term>(1, 0)
        try {
            m.solveLGSwithCholesky((b))
            fail()
        } catch (_: NoDecompositionException) {}
    }
}