package algebra

import analysis.terms.model.Num
import analysis.terms.model.Term
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

internal class MatrixTest {

    private val i3 = Matrix<Term>(Vec(1, 0, 0), Vec(0, 1, 0), Vec(0, 0, 1))
    private val i4 = Matrix<Term>(Vec(1, 0, 0, 0), Vec(0, 1, 0, 0), Vec(0, 0, 1, 0), Vec(0, 0, 0, 1))
    private val m34 = Matrix<Term>(Vec(3.0, -1.0, 1.9, 2.0), Vec(0, 0, -5, -2), Vec(1.0, 5.0, 1.62, -1.1))
    private val m33 = Matrix<Term>(Vec(3.0, -1.0, 1.9), Vec(0, 0, -5), Vec(1.0, 5.0, 1.62))
    private val m44 =
        Matrix<Term>(
            Vec(3.0, -1.0, 1.9, 2.0),
            Vec(-1, 0, -5, -2),
            Vec(1.9, -5.0, 1.62, -1.1),
            Vec(2.0, -2.0, -1.1, -2.0)
        )
    private val m33timesM34 = Matrix<Term>(
        Vec(10.9, 6.5, 13.778, 5.91),
        Vec(-5.0, -25.0, -8.1, 5.5),
        Vec(4.62, 7.1, -20.47560, -9.782)
    )
    private val invertable = Matrix<Term>(Vec(5, 6), Vec(4, 5))
    private val invert = Matrix<Term>(Vec(5, -6), Vec(-4, 5))
    private val invertable1 = Matrix<Term>(Vec(1, 1, 0), Vec(2, 0, 1), Vec(0, 2, 2))
    private val invert1 = Matrix(
        Vec(Num(1, 3), Num(1, 3), Num(-1, 6)),
        Vec(Num(2, 3), Num(-1, 3), Num(1, 6)),
        Vec(Num(-2, 3), Num(1, 3), Num(1, 3))
    )
    private val invertable2 = Matrix<Term>(Vec(1, 1, 0, 100), Vec(2, 0, 1, 1), Vec(0, 2, 2, 2), Vec(10, 10000, -1, 0))
    private val invert2 = "|     1 / -2001197,  1000099 / 2001197,   90909 / -363854,   100 / 2001197|\n" +
            "|     2 / -2001197,     999 / -2001197,      109 / 363854,   200 / 2001197|\n" +
            "| 20010 / -2001197,  -10990 / -2001197,  -90455 / -181927,  197 / -2001197|\n" +
            "|-20012 / -2001197,    9991 / -2001197,    -454 / -181927,    3 / -2001197|"

    @Test
    fun getHeight() {
        assertEquals(3, i3.height)
        assertEquals(4, i4.height)
        assertEquals(3, m34.height)
        assertEquals(3, m33.height)
        assertEquals(4, m44.height)
    }

    @Test
    fun getWidth() {
        assertEquals(3, i3.width)
        assertEquals(4, i4.width)
        assertEquals(4, m34.width)
        assertEquals(3, m33.width)
        assertEquals(4, m44.width)
    }

    @Test
    fun getSymmetrical() {
        assertTrue(i3.symmetrical)
        assertTrue(i4.symmetrical)
        assertFalse(m33.symmetrical)
        assertFalse(m34.symmetrical)
        assertTrue(m44.symmetrical)
    }

    @Test
    fun getOrthogonal() {
        assertTrue(i3.orthogonal)
        assertTrue(i4.orthogonal)
        assertFalse(m33.orthogonal)
        assertFalse(m34.orthogonal)
        assertFalse(m44.orthogonal)
    }

    @Test
    fun testCols() {
        assertEquals(m34.transpose().toList(), m34.cols)
    }

    @Test
    fun testSubMatrix() {
        assertEquals(
            m44.subMatrix(1, 3, 1, 3),
            Matrix(Vec(0, -5), Vec(-5.0, 1.62))
        )
    }

    @Test
    fun getDeterminant() {
        assertEquals(i3.oneElement, i3.determinant)
        assertEquals(i4.oneElement, i3.determinant)
        assertEquals(Num(80), m33.determinant)
        assertEquals(Num(0), m34.determinant)
        assertEquals(Num(112.77), m44.determinant)
    }

    @Test
    fun getRegular() {
        assertTrue(i3.regular)
        assertTrue(i4.regular)
        assertTrue(m33.regular)
        assertTrue(m44.regular)
        assertFalse(m34.regular)
    }

    @Test
    fun times() {
        assertEquals(m33, m33 * i3)
        assertEquals(m33, i3 * m33)
        assertEquals(m44, m44 * i4)
        assertEquals(m44, i4 * m44)
        assertEquals(m33timesM34, m33 * m34)
        println(m33 * m34)
        println(Num(-405, 50))
        try {
            m33 * i4
            fail("Wrong dimensions")
        } catch (_: Exception) {
        }
    }

    @Test
    fun div() {
    }

    @Test
    fun plus() {
    }

    @Test
    fun minus() {
    }

    @Test
    fun zero() {
        val zero = m34.zero()
        assertEquals(zero.width, m34.width)
        assertEquals(zero.height, m34.height)
        assertTrue(zero.elements().all { it.equals(0.0) })
    }

    @Test
    fun one() {
        val one = m34.one()
        assertEquals(one.width, m34.width)
        assertEquals(one.height, m34.height)
        for (row in 0 until one.height) {
            for (column in 0 until one.width) {
                if (row == column) assertTrue(one[row][column].equals(1))
                else assertTrue(one[row][column].equals(0.0))
            }
        }
    }

    @Test
    fun inverseMult() {
        assertEquals(invert, invertable.inverseMult())
        assertEquals(invert1, invertable1.inverseMult())
        println(invertable2.inverseMult())
        println("must equal")
        println(invert2)
        /*assertEquals(i3.one(), i3 * i3.inverseMult(i3))
        assertEquals(i4.one(), i4 * i4.inverseMult(i4))
        assertTrue(Frobeniusnorm2<Num>().l(m44.one() - m44 * m44.inverseMult(m44)) < 0.0001)
        assertTrue(Frobeniusnorm2<Num>().l(m33.one() - m33 * m33.inverseMult(m33)) < 0.0001)
        try {
            assertEquals(m34.one(), m34 * m34.inverseMult(m34))
            fail()
        } catch (_: Exception) {}*/
    }

    @Test
    fun inverseAdd() {
        assertEquals(m34.zero(), m34 + m34.inverseAdd())
        assertEquals(m44.zero(), m44 + m44.inverseAdd())
        assertEquals(m33.zero(), m33 + m33.inverseAdd())
        assertEquals(i3.zero(), i3 + i3.inverseAdd())
        assertEquals(i4.zero(), i4 + i4.inverseAdd())
    }

    @Test
    fun toStaircase() {
    }

    @Test
    fun elements() {
        for (i in 0 until m34.height) {
            for (j in 0 until m34.width) {
                assertEquals(m34[i][j], m34.elements()[i * m34.width + j])
            }
        }
    }
}