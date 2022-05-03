package org.deg.uni.analysis.terms

import org.deg.uni.analysis.terms.model.*
import org.deg.uni.analysis.terms.simplifying.P
import org.deg.uni.analysis.terms.simplifying.Pow
import org.deg.uni.analysis.terms.simplifying.S
import org.deg.uni.analysis.unaryMinus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TermParserTest {
    @Test
    fun testParse1() {
        assertParsesTo("x - x", S(x, P(x, -one)))
    }

    @Test
    fun testParse2() {
        assertParsesTo("x / x", P(x, Pow(x, -one)))
    }

    @Test
    fun testParse3() {
        assertParsesTo("x + 2 * x", S(x, P(x, two)))
    }

    @Test
    fun testParse4() {
        assertParsesTo("2^x+4", S(Pow(two, x), four))
    }

    @Test
    fun testParse5() {
        assertParsesTo("(x)", x)
    }

    @Test
    fun testParse6() {
        assertParsesTo("((x))", x)
    }

    @Test
    fun testParse7() {
        assertParsesTo("2^-1.290388477836", Pow(two, Num(-1.290388477836)))
    }

    @Test
    fun testParse8() {
        assertParsesTo("xy", P(x, y))
    }

    @Test
    fun testParse9() {
        assertParsesTo("x(y + 2)", P(x, S(y, two)))
    }

    @Test
    fun testParse10() {
        assertParsesTo("Log(2)(y + 2)^3", Pow(Log(two, S(y, two)), three))
    }

    @Test
    fun testParse11() {
        assertParsesTo("Log(2)((y + 2)^3)", Log(two, Pow(S(y, two), three)))
    }

    @Test
    fun testParse12() {
        assertParsesTo("2^xyzr", P(Pow(two, x), y, z, Variable("r")))
    }

    @Test
    fun testParse13() {
        assertParsesTo("2x + y + z - r", S(P(two, x), y, z, P(-one, Variable("r"))))
    }

    @Test
    fun testParse14() {
        assertParsesTo("(x+y)(x-y)", P(S(x, y), S(x, P(y, -one))))
    }

    @Test
    fun testParse15() {
        assertParsesTo("x^y^z", Pow(x, Pow(y, z)))
    }

    @Test
    fun testParse16() {
        assertParsesTo("(z + y)x", P(x, S(y, z)))
    }

    @Test
    fun testParse17() {
        assertParsesTo("x^0 * -1", P(Pow(x, zero), -one))
    }

    @Test
    fun testFail1() {
        assertParsesTo("(x", x, false)
    }

    @Test
    fun testFail1_5() {
        assertParsesTo("+-", x, false)
    }

    @Test
    fun testFail2() {
        assertParsesTo("()", x, false)
    }

    @Test
    fun testFail3() {
        assertParsesTo("3 + ) 4", x, false)
    }

    @Test
    fun testFail5() {
        assertParsesTo("3x ++)", x, false)
    }

    @Test
    fun testFail6() {
        assertParsesTo("3x ++", x, false)
    }

    private fun assertParsesTo(str: String, term: Term, success: Boolean = true) {
        try {
            val parsed = str.toTerm()
            assertEquals(term, parsed)
            assertTrue(success, "expected test to fail, but got results: $str -> $parsed")
            println("Parsing success on $str, simplifies to ${parsed.simplify()}")
        } catch (e: java.lang.IllegalStateException) {
            assertFalse(success, e.stackTraceToString())
            println("Parsing of $str failed successfully with stacktrace ${e.stackTraceToString()}")
        }
    }
}