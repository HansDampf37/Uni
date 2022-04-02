package propa

import analysis.terms.*
import analysis.terms.simplifying.UnificationVariable
import analysis.terms.simplifying.a
import analysis.terms.simplifying.b
import analysis.terms.simplifying.c
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class UnificationTest {
    @Test
    fun testUnification0() {
        val x = UnificationVariable("x")
        val y = UnificationVariable("y")
        val t = Power(two, Sum(a, Num(3)))
        val u = Power(x, y)
        val result = Unifier<Term>().unify(t, u)
        println("Unification of terms\n$t and \n$u is \n$result")
        Assertions.assertEquals(listOf(mapOf(Pair(y, Sum(a, Num(3))), Pair(x, Num(2)))), result)
    }

    @Test
    fun testUnification1() {
        val x = UnificationVariable("x")
        val y = UnificationVariable("y")
        val z = UnificationVariable("z")
        val t = Sum(a, b, c)
        val u = Sum(x, y, z)
        val result = Unifier<Term>().unify(t, u)
        println("Unification of terms\n$t and \n$u is \n$result")
        Assertions.assertEquals(6, result.size)
    }

    @Test
    fun testUnification2() {
        val x = UnificationVariable("x")
        val y = UnificationVariable("y")
        val z = UnificationVariable("z")
        val t = Sum(Product(two, a), Product(two, b))
        val u = Sum(Product(x, y), Product(x, z))
        val result = Unifier<Term>().unify(t, u)
        println("Unification of terms\n$t and \n$u is \n$result")
        Assertions.assertEquals(listOf(
            mapOf(Pair(z, b), Pair(y, a), Pair(x, Num(2))),
            mapOf(Pair(z, a), Pair(y, b), Pair(x, Num(2)))
        ), result)
    }

    @Test
    fun testUnification3() {
        val x = UnificationVariable("x")
        val y = UnificationVariable("y")
        val z = UnificationVariable("z")
        val t = Sum(Product(two, a), Product(two, b))
        val u = Sum(Product(y, x), Product(z, x))
        val result = Unifier<Term>().unify(t, u)
        println("Unification of terms\n$t and \n$u is \n$result")
        Assertions.assertEquals(listOf(
            mapOf(Pair(z, b), Pair(y, a), Pair(x, Num(2))),
            mapOf(Pair(z, a), Pair(y, b), Pair(x, Num(2)))
        ), result)
    }

    @Test
    fun testUnification4() {
        val f = UnificationVariable("f", filler = true)
        val t = Product(one, a, b)
        val u = Product(f, one)
        val result = Unifier<Term>().unify(t, u)
        println("Unification of terms\n$t and \n$u is \n$result")
        Assertions.assertEquals(listOf(
            mapOf(Pair(f, a * b)),
            mapOf(Pair(f, b * a)),
        ), result)
    }
}