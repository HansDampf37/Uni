package org.deg.uni.analysis.terms.simplifying

import org.deg.uni.analysis.terms.model.*
import org.deg.uni.analysis.terms.*
import org.deg.uni.analysis.unaryMinus
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
        val expected = one
        assertSimplifiesTo(product, expected)
    }

    @Test
    fun testSimplifyTimes5() {
        val product = P(x, x)
        assertSimplifiesTo(product, Pow(x, two))
    }

    @Test
    fun testSimplifyTimes6() {
        val product = P(x, Num(3), x)
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
        val res = P(two, Log(Num(3), x))
        assertSimplifiesTo(term, res)
    }

    @Test
    fun testLog1() {
        val term = Sum(Ln(x), Ln(P(two, x)))
        val expected = Ln(P(two, Pow(x, two)))
        assertSimplifiesTo(term, expected)
    }

    @Test
    fun testLog2() {
        val term = Ln(E)
        assertSimplifiesTo(term, one)
    }

    @Test
    fun testLog3() {
        val term = P(Ln(E), x)
        assertSimplifiesTo(term, x)
    }

    @Test
    fun testCalc() {
        assertSimplifiesTo(P(Num(12), Pow(Num(12), Num(-1))), one)
        println((Num(-8) * Pow(Num(12), Num(-1)) + Num(4)).simplify())
    }

    @Test
    fun testCalc2() {
        val term = P(Pow(Num(12), one), Pow(-two, -one))
        println(term)
        assertSimplifiesTo(term, -six)
    }

    @Test
    fun testCalc3() {
        val term = "(2 * x) * 2^x + x^2 * (LogE(2) * 2^x)".toTerm()
        println(term)
        assertSimplifiesTo(term, "2^x * x(2 + LogE(2) * x)".toTerm())
    }

    @Test
    fun testCalc4() {
        val term = "x^2 - 2".toTerm()
        x.value = Num(1)
        println(term)
        assertSimplifiesTo(term, Num(-1))
    }

    private fun assertSimplifiesTo(complex: Term, simplified: Term) {
        val res = complex.simplify()
        println("replace: $complex         (${complex.quality()}) \nby:      $res         (${res.quality()})")
        Assertions.assertEquals(simplified, res, "Expected simplification: $simplified     (${simplified.quality()})")
    }
}