package analysis.terms.simplifying

import analysis.inverseMult
import analysis.terms.*
import analysis.unaryMinus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SimplifierTest {
    @Test
    fun testSimplifyTimes1() {
        val product = Product(x, Power(x, Num(-1)))
        assertSimplifiesTo(product, one)
    }

    @Test
    fun testSimplifyTimes2() {
        val product = Product(x, Product(-one, Power(x, Num(-1))))
        assertSimplifiesTo(product, -one)
    }

    @Test
    fun testSimplifyTimes3() {
        val product =
            P(four, x, Pow(two, -one), x, Pow(x, -two))
        assertSimplifiesTo(product, two)
    }

    @Test
    fun testSimplifyTimes4() {
        val product = Product(Power(two, x), Power(Power(two, x), -one))
        val expected = Pow(Num(5, 2), x)
        assertSimplifiesTo(product, expected)
    }

    @Test
    fun testSimplifyTimes5() {
        val product = x * x
        assertSimplifiesTo(product, Pow(x, two))
    }

    @Test
    fun testSimplifyTimes6() {
        val product = x * Num(3) * x
        val result = P(Num(3), Pow(x, two))
        assertSimplifiesTo(product, result)
    }

    @Test
    fun testSimplifyPlus1() {
        val sum = Sum(x, Product(x, Num(-1)))
        assertSimplifiesTo(sum, zero)
    }

    @Test
    fun testSimplifyPlus2() {
        val sum = Sum(x, y, Product(x, Num(-4)))
        assertSimplifiesTo(sum, S(P(-three, x), y))
    }

    @Test
    fun testSimplifyPlus3() {
        val sum = Sum(Sum(Sum(x) as Term) as Term)
        assertSimplifiesTo(sum, x)
    }

    @Test
    fun testSimplifyPlus4() {
        val sum = Sum(Sum(x, y) as Term, Sum(Product(Num(-1), x), Product(Num(-1), y)))
        assertSimplifiesTo(sum, zero)
    }

    @Test
    fun testSimplifyPlus5() {
        val sum = Sum(Power(x, two), Product(Power(x, two), two))
        val res = Product(three, Power(x, two))
        assertSimplifiesTo(sum, res)
    }

    @Test
    fun testSimplifyPlus6() {
        val sum = Sum(Product(Power(x, two), two), Product(Power(x, two), two))
        val res = Product(Power(x, two), four)
        assertSimplifiesTo(sum, res)
    }

    @Test
    fun testDiv1() {
        val product = Product(Num(-1), x, x.inverseMult())
        assertSimplifiesTo(product, -one)
    }

    @Test
    fun testPower() {
        val p1 = Power(five, two)
        assertSimplifiesTo(p1, Num(25))
    }

    @Test
    fun testPower2() {
        val p1 = Num(5).sqrt()
        val p2 = p1.clone()
        val p3 = p1.clone()
        val p4 = p1.clone()
        val p5 = p1.clone()
        val sum = Sum(p1, p2, p3, p4, p5)
        assertSimplifiesTo(sum, Power(Num(5), Num(3, 2)))
    }

    @Test
    fun testLog() {
        val term = Sum(Log(Num(3), x), Log(Num(3), x))
        val res = two * Log(Num(3), x)
        assertSimplifiesTo(term, res)
    }

    @Test
    fun testLog1() {
        val term = Sum(Ln(x), Ln(P(two, x)))
        val expected = Ln(P(two, Pow(x, two)))
        val expected1 = S(one, P(two, Ln(x)))
        assertSimplifiesTo(term, expected)
    }
    
    @Test
    fun test

    private fun assertSimplifiesTo(complex: Term, simplified: Term) {
        val res = complex.simplify()
        println("replace: $complex         (${complex.quality()}) \nby:      $res         (${res.quality()})")
        Assertions.assertEquals(simplified, res, "Expected simplification: $simplified     (${simplified.quality()})")
    }
}