package propa

import analysis.terms.*
import analysis.terms.simplifying.UnificationVariable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class UnificationTest {
    private val two = Num(2)
    private val a = Variable("a")
    private val b = Variable("b")

    @Test
    fun testUnification1() {
        val x = UnificationVariable("x")
        val y = UnificationVariable("y")
        val t = Power(two, Sum(a, Num(3)))
        val u = Power(x, y)
        val result = t.unify(u)
        println("Unification of terms\n$t and \n$u is \n$result")
        Assertions.assertEquals(two, result.second[x])
        Assertions.assertEquals(Sum(a, Num(3)), result.second[y])
    }

    @Test
    fun testUnification2() {
        val x = UnificationVariable("x")
        val y = UnificationVariable("y")
        val z = UnificationVariable("z")
        val t = Sum(Product(two, a), Product(two, b))
        val u = Sum(Product(x, y), Product(x, z))
        val result = t.unify(u)
        println("Unification of terms\n$t and \n$u is \n$result")
        Assertions.assertEquals(two, result.second[x])
        Assertions.assertEquals(a, result.second[y])
        Assertions.assertEquals(b, result.second[z])
    }

    @Test
    fun testUnification3() {
        val x = UnificationVariable("x")
        val y = UnificationVariable("y")
        val z = UnificationVariable("z")
        val t = Sum(Product(two, a), Product(two, b))
        val u = Sum(Product(y, x), Product(z, x))
        val result = t.unify(u)
        Assertions.assertEquals(two, result.second[x])
        println("Unification of terms\n$t and \n$u is \n$result")
    }
}