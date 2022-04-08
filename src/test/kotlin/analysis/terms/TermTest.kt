package analysis.terms

import analysis.terms.model.*
import analysis.unaryMinus
import junit.framework.TestCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TermTest {
    @Test
    fun test1() {
        val x = Variable("x")
        val p = Product(Product(x, Num(-1)), Power(x, Num(-1)))
        assertEquals(Num(-1), p.simplify())
    }

    @Test
    fun testDiv() {
        val x = Variable("x")
        assertEquals(Num(1), x / x)
        assertEquals(Num(-1), -x / x)
    }
    fun testEqualsSum() {
        assertEquals(Sum(Variable("x"), Variable("y")), Sum(Variable("x"), Variable("y")))
        assertEquals(Sum(Variable("x"), Variable("y")), Sum(Variable("y"), Variable("x")))
        assertNotEquals(Sum(Variable("x"), Variable("y")), Sum(Variable("y"), Variable("z")))
    }

    @Test
    fun testEqualsProduct() {
        assertEquals(Product(Variable("x"), Variable("y")), Product(Variable("x"), Variable("y")))
        assertEquals(Product(Variable("x"), Variable("y")), Product(Variable("y"), Variable("x")))
        assertNotEquals(Product(Variable("x"), Variable("y")), Product(Variable("y"), Variable("z")))
    }
}