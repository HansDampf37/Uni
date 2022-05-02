package org.deg.uni.analysis.terms.simplifying

import org.deg.uni.analysis.terms.model.*
import org.deg.uni.analysis.terms.*
import org.deg.uni.unification.Unifier
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

    @Test
    fun testUnification5() {
        val f1 = UnificationVariable("f1", filler = true)
        val f2 = UnificationVariable("f2", filler = true)
        // val t = "2^x * x * 2 + LogE(2) * x^2 * 1 / 2 * 2^x * 2".toTerm()
        //val t = ("ab * 2 + c * d * 1/2 * e * 2").toTerm()
        val t = ("abcd + abcd").toTerm()
        val u = S(P(a, f1), P(a, f2))
        val result = Unifier<Term>().unify(t, u)
        println(result.size)
        println("Unification1 of terms\n$t and \n$u is \n$result")
        Assertions.assertEquals(listOf(
            mapOf(Pair(f1, a * b)),
            mapOf(Pair(f2, b * a)),
        ), result)
    }
}