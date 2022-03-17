package analysis.functions

import analysis.terms.Num
import analysis.terms.Power
import analysis.terms.Product
import analysis.terms.Variable
import analysis.unaryMinus
import junit.framework.TestCase
import org.junit.jupiter.api.Test


internal class FTest : TestCase() {

    private val two = Num(2)
    private val three = Num(3)

    @Test
    fun evaluate() {
        val x = Variable("x")
        val f = F(x) { x * x }
        assertEquals(Num(9), f.evaluate(mapOf(Pair(x, Num(3)))))
    }

    @Test
    fun evaluate2() {
        val x = Variable("x")
        val y = Variable("y")
        val f = F(x, y) { x * y * y }
        assertEquals(Num(12), f.evaluate(mapOf(Pair(x, Num(3)), Pair(y, Num(2)))))
    }

    @Test
    fun testCut() {
        val x = Variable("x")
        val y = Variable("y")
        val f = F(x, y) { Num(2) * x - Num(3) }
        val g = F(x, y) { Num(-1) * (x + Num(2)) }
        assertEquals(listOf(mutableMapOf(Pair(x, Num(1, 3)))), f.cuts(g))
    }

    @Test
    fun testCut2() {
        val x = Variable("x")
        val f = F(x) { Num(2) * Power(x, Num(2)) }
        val g = F(x) { Num(-2) * Power(x, Num(2)) + Num(2) }
        val cuts = f.cuts(g)
        println("$f cuts $g for $cuts")
        assertEquals(listOf(mutableMapOf(Pair(x, Power(Num(1, 2), Num(1, 2))))), cuts)
    }

    @Test
    fun testCut3() {
        // val x = Variable("x")
        //val f = F(x) { (x - Num(2)) * (x - Num(3)) }
        //val g = F(x) { Num(0) }
        // TODO
        //val cuts = f.cuts(g)
        //println("$f cuts $g for $cuts")
        //assertEquals(listOf(mutableMapOf(Pair(x, Num(2))), mutableMapOf(Pair(x, Num(3)))), cuts)
    }

    @Test
    fun testCut4() {
        val x = Variable("x")
        val f = F(x) { two * x.pow(8) + three * x.pow(4) }
        val g = F(x) { Num(0) }
        val cuts = f.cuts(g)
        // TODO
        println("$f cuts $g for $cuts")
        assertEquals(listOf(mutableMapOf(Pair(x, Num(0))), mutableMapOf(Pair(x, Num(1)))), cuts)
    }

    @Test
    fun testDerive() {
        val x = Variable("x")
        val y = Variable("y")
        val f = F(x, y) { Num(2) * x - Num(3) }
        val g = F(x, y) { Num(-1) * (x + Num(2)) }
        println("$f -> ${f.derive(x)}")
        println("$g -> ${g.derive(y)}")
    }
}