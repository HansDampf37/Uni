package analysis.terms

import junit.framework.TestCase
import org.junit.jupiter.api.Test

internal class NumTest: TestCase() {

    @Test
    fun inverseAdd() {
        assertEquals(Num(1, 2), Num(-1, 2).inverseAdd())
        assertEquals(Num(1, 2), Num(1, -2).inverseAdd())
    }

    @Test
    fun inverseMult() {
        assertEquals(Num(1, 2), Num(2).inverseMult())
        assertEquals(Num(1, -2), Num(-2).inverseMult())
    }

    @Test
    fun zero() {
        assertEquals(Num(0), Num(100.02).zero())
    }

    @Test
    fun one() {
        assertEquals(Num(1), Num(100.02).one())
    }

    @Test
    fun plus() {
        val a = Num(1, 3)
        val b = Num(1, 6)
        assertEquals(Num(1), a + a + a)
        assertEquals(Num(1, 2), a + b)
        assertEquals(Num(1, 6), a - b)
        assertEquals(Num(0), a - a)
    }

    @Test
    fun times() {
        val a = Num(1, 3)
        val b = Num(1, 4)
        val c = Num(24, 2)
        assertEquals(Num(1), a * b * c)
        assertEquals(Num(4, 3), a / b)
        assertEquals(Num(1, 9), a / b / c)
        assertEquals(Num(109, 9),c + a / b / c)
        println(c + a / b / c)
    }

    @Test
    fun equals() {
        val a = Num(2, 3)
        val b = Num(2, 1)
        val c = Num(5, 4)
        val d = Num(4, 6)
        assertTrue(a.equals(2.0 / 3.0))
        assertTrue(b.toDouble() == 2.0)
        assertTrue(c.toDouble() == 5.0 / 4.0)
        assertEquals(a, d)
    }

    @Test
    fun compare() {
        assertTrue(Num(1, 2) < 1)
        assertFalse(Num(1, 2) < 0.5)
        assertTrue(Num(1, 2) > -1)
    }

    @Test
    fun test() {
        println(3 * 1.9)
        println((Num(3) * Num(19, 10)).toDouble())
        println((Num(3) * Num(1.9)).toDouble())
        assertEquals(Num(13.778), Num(3) * Num(1.9) + Num(-1) * Num(-5) + Num(1.9) * Num(1.62))
    }

    @Test
    fun testShorten() {
        assertEquals(Num(6,9).denominator, 3.0)
        assertEquals(Num(6,9).num, 2.0)
    }
}