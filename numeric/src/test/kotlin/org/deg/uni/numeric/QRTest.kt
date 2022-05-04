package org.deg.uni.numeric

import org.deg.uni.algebra.Matrix
import org.deg.uni.algebra.Vec
import org.deg.uni.analysis.terms.model.Term
import org.junit.jupiter.api.Test

internal class QRTest {

    @Test
    fun decomp() {
        val m = Matrix<Term>(
            Vec(1, -1, 4),
            Vec(1, 4, -2),
            Vec(1, 4, 2),
            Vec(1, -1, 0)
        )
        val q = m.qrDecomp()
        println(QR().getQR(q))
        println(m)
        println(q)
    }
}