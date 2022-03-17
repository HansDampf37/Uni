package analysis.terms

import analysis.unaryMinus
import junit.framework.TestCase

class DerivativeTest : TestCase() {
    private val x = Variable("x")
    private val y = Variable("y")
    private val one = Num(1)
    private val two = Num(2)
    private val three = Num(3)

    fun test1() {
        val term = x
        println("($term)/dx = ${term.derive(x)}")
        assertEquals(one, term.derive(x))
        assertEquals(Num(0), term.derive(y))
    }

    fun test2() {
        val term = two * x
        println("($term)/dx = ${term.derive(x)}")
        assertEquals(two, term.derive(x))
        assertEquals(Num(0), term.derive(y))
    }

    fun test3() {
        val term = x + two * y
        println("($term)/dx = ${term.derive(x)}")
        assertEquals(one, term.derive(x))
        assertEquals(two, term.derive(y))
    }

    fun test3_1() {
        val term = three * x.pow(three) + two * y
        println("($term)/dx = ${term.derive(x)}")
        assertEquals(Num(9) * x.pow(two), term.derive(x))
        assertEquals(two, term.derive(y))
    }

    fun test4() {
        val term = x.pow(2) * two.pow(x)
        println("($term)/dx = ${term.derive(x)}")
        assertEquals(two * x * two.pow(x) + x.pow(2) * Variable("ln($two)") * two.pow(x), term.derive(x))
        assertEquals(Num(0), term.derive(y))
    }

    fun test5() {
        val term = x * Power(two, x) * Power(y, x)
        println("($term)/dx = ${term.derive(x)}")
        assertEquals(
            y.pow(x) * (x * Variable("ln(2)") * two.pow(x) + two.pow(x)) +
                    two.pow(x) * x * Variable("ln(y)") * y.pow(x), term.derive(x)
        )
        println("($term)/dy = ${term.derive(y)}")
        assertEquals(x.pow(two) * Power(two, x) * Power(y, x - one), term.derive(y))
    }

    fun test6() {
        val term = two.pow(x.pow(y))
        println("($term)/dx = ${term.derive(x)}")

        assertEquals(
            two.pow(x.pow(y)) * x.pow(-one + y) * y * Variable("ln(2)"), term.derive(x)
        )
        println("($term)/dy = ${term.derive(y)}")
        assertEquals(two.pow(x.pow(y)) * x.pow(y) * Variable("ln(2)") * Variable("ln(x)"), term.derive(y))
    }

    fun test7() {
        val term = two * x.pow(8) + three * x.pow(4)
        println("($term)/dx = ${term.derive(x)}")
        assertEquals(
            Num(16) * x.pow(7) + Num(12) * x.pow(3), term.derive(x)
        )
    }
}