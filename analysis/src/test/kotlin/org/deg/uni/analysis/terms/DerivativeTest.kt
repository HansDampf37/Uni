package org.deg.uni.analysis.terms

import org.deg.uni.analysis.terms.model.*
import org.deg.uni.analysis.terms.simplifying.P
import org.deg.uni.analysis.terms.simplifying.S
import org.deg.uni.analysis.terms.simplifying.Pow
import org.deg.uni.analysis.terms.simplifying.quality
import org.deg.uni.analysis.unaryMinus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DerivativeTest {
    @Test
    fun test1() {
        val term = x
        assertDerivative(term, listOf(x, y), listOf(one, zero))
    }

    @Test
    fun test2() {
        val term = two * x
        assertDerivative(term, listOf(x, y), listOf(two, zero))
    }

    @Test
    fun test3() {
        val term = x + two * y
        assertDerivative(term, listOf(x, y), listOf(one, two))
    }

    @Test
    fun test3_1() {
        val term = S(P(three, x.pow(three)), P(two, y))
        assertDerivative(term, listOf(x, y), listOf(Num(9) * x.pow(two), two))
    }

    @Test
    fun test4() {
        val term = P(Pow(x, two), Pow(two, x))
        assertDerivative(term, listOf(x, y), listOf(P(Pow(two, x), x, S(two, P(x, Ln(two)))), zero))
    }

    @Test
    fun test5() {
        val term = P(x, Power(two, x), Power(y, x))
        assertDerivative(
            term, listOf(x, y), listOf(
                P(Pow(P(two, y), x), S(one, P(x, Ln(P(two, y))))),
                x.pow(two) * Power(two, x) * Power(y, x - one)
            )
        )
    }

    @Test
    fun test6() {
        val term = two.pow(x.pow(y))
        assertDerivative(
            term, listOf(x, y), listOf(
                P(two.pow(x.pow(y)), x.pow(S(-one, y)), y, Ln(two)),
                P(two.pow(x.pow(y)), x.pow(y), Ln(x), Ln(two))
            )
        )
    }

    @Test
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
            Assertions.assertEquals(
                expectedDerivatives[i],
                calculatedDerivatives[i],
                "Expected derivative's quality ${expectedDerivatives[i].quality()}\nCalculated derivative's quality ${calculatedDerivatives[i].quality()}"
            )
        }
    }
}