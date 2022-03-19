package analysis.terms

import analysis.terms.simplifying.zero
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
        assertDerivative(term, listOf(x, y), listOf(one, zero))
    }

    fun test2() {
        val term = two * x
        assertDerivative(term, listOf(x, y), listOf(two, zero))
    }

    fun test3() {
        val term = x + two * y
        assertDerivative(term, listOf(x, y), listOf(one, two))
    }

    fun test3_1() {
        val term = three * x.pow(three) + two * y
        assertDerivative(term, listOf(x, y), listOf(Num(9) * x.pow(two), two))
    }

    fun test4() {
        val term = x.pow(2) * two.pow(x)
        assertDerivative(term, listOf(x, y), listOf(two * x * two.pow(x) + x.pow(2) * two.pow(x), zero))
    }

    fun test5() {
        val term = x * Power(two, x) * Power(y, x)
        assertDerivative(
            term, listOf(x, y), listOf(
                y.pow(x) * (x * two.pow(x) + two.pow(x)) + two.pow(x) * x * Ln(y) * y.pow(x),
                x.pow(two) * Power(two, x) * Power(y, x - one)
            )
        )
    }

    fun test6() {
        val term = two.pow(x.pow(y))
        assertDerivative(
            term, listOf(x, y), listOf(
                two.pow(x.pow(y)) * x.pow(-one + y) * y,
                two.pow(x.pow(y)) * x.pow(y) * Ln(x)
            )
        )
    }

    fun test7() {
        val term = two * x.pow(8) + three * x.pow(4)
        assertDerivative(
            term, listOf(x), listOf(
                Num(16) * x.pow(7) + Num(12) * x.pow(3)
            )
        )
    }

    fun assertDerivative(term: Term, variables: List<Variable>, expectedDerivatives: List<Term>) {
        val calculatedDerivatives = variables.map { term.derive(it) }
        for (i in variables.indices) {
            println("d/d${variables[i]} $term = ${calculatedDerivatives[i]}")
            assertEquals(expectedDerivatives[i], calculatedDerivatives[i].simplify())
        }
    }
}