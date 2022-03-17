package analysis.terms

import analysis.inverseMult
import analysis.unaryMinus
import junit.framework.TestCase
import org.junit.jupiter.api.Test

internal class SimplifierTest: TestCase() {
    private val x = Variable("x")
    private val one = Num(1)
    private val two = Num(2)
    private val four = Num(4)

    @Test
    fun testSimplifyTimes1() {
        val product = Product(x, Power(x, Num(-1)))
        val simp = product.simplify()
        println("$product -> $simp")
        assertEquals(one, simp)
    }

    @Test
    fun testSimplifyTimes2() {
        val product = Product(x, Product(-one, Power(x, Num(-1))))
        val simp = product.simplify()
        println("$product -> $simp")
        assertEquals(-one, simp)
    }

    @Test
    fun testSimplifyTimes3() {
        val product =
            Product(four, x, Power(two, -one), x, Power(x, Num(-2)))
        val simp = product.simplify()
        println("$product -> $simp")
        assertEquals(two, simp)
    }

    @Test
    fun testSimplifyTimes4() {
        val product = Product(Power(two, x))
        val res = product / product.clone()
        val simp = res.simplify()
        println("$res -> $simp")
        assertEquals(one, simp)
    }

    @Test
    fun testSimplifyTimes5() {
        val product = x * x
        println("${Product(x, x)} -> $product")
        assertEquals(x.pow(2), product)
    }

    @Test
    fun testSimplifyTimes6() {
        val product = x *  Num(3) * x
        println("${Product(x, Num(3), x)} -> $product")
        assertEquals(Num(3) * x.pow(2), product)
    }

    @Test
    fun testSimplifyPlus1() {
        val sum = Sum(x, Product(x, Num(-1)))
        val simp = sum.simplify()
        println("$sum -> $simp")
        assertEquals(Num(0), simp)
    }

    @Test
    fun testSimplifyPlus2() {
        val sum = Sum(x, Variable("y"), Product(x, Num(-4)))
        val simp = sum.simplify()
        println("$sum -> $simp")
        assertEquals(Sum(Product(x, Num(-3)), Variable("y")), simp)
    }

    @Test
    fun testSimplifyPlus3() {
        val sum = Sum(Sum(Sum(x) as Term) as Term)
        val simp = sum.simplify()
        println("$sum -> $simp")
        assertEquals(x, simp)
    }

    @Test
    fun testSimplifyPlus4() {
        val sum = Sum(Sum(x, Variable("y")) as Term, Sum(Product(Num(-1), x), Product(Num(-1), Variable("y"))))
        val simp = sum.simplify()
        println("$sum -> $simp")
        assertEquals(Num(0), simp)
    }

    @Test
    fun testSimplifyPlus5() {
        val sum = Sum(Power(x, two), Product(Power(x, two), two))
        val simp = sum.simplify()
        println("$sum -> $simp")
        assertEquals(Product(Num(3), Power(x, two)), simp)
    }

    @Test
    fun testSimplifyPlus6() {
        val sum = Sum(Product(Power(x, two), two), Product(Power(x, two), two))
        val simp = sum.simplify()
        println("$sum -> $simp")
        assertEquals(Product(Power(x, two), four), simp)
    }

    @Test
    fun testDiv1() {
        val x = x
        val product = Product(Num(-1), x)
        val result = product.clone()
        result.add(Power(x, Num(-1)))
        assertEquals(result, product.apply{ add(x.inverseMult()) })
        assertEquals(Product(Num(-1), x, Power(x, Num(-1))).simplify(), Num(-1))
    }

    @Test
    fun testPower() {
        val p1 = Power(Num(5), two)
        val simp1 = Num(25)
        assertEquals(simp1, p1.simplify())
        val p2 = Power(Num(5), Num(1, 2))
        assertEquals(p2, p2.simplify())
    }

    @Test
    fun testPower2() {
        val p1 = Num(5).sqrt()
        val p2 = p1.clone()
        val p3 = p1.clone()
        val p4 = p1.clone()
        val p5 = p1.clone()
        assertEquals(Power(Num(5), Num(3, 2)), (p1 + p2 + p3 + p4 + p5).simplify())
        println((p1 + p2 + p3 + p4 + p5).toDouble())
    }

}