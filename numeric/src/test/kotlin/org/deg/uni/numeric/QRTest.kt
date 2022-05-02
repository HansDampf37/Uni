package numerik

import algebra.Matrix
import algebra.Vec
import analysis.terms.model.Term
import org.deg.uni.numeric.QR
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