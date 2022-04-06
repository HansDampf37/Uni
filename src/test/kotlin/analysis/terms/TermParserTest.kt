package analysis.terms

import analysis.terms.simplifying.P
import analysis.terms.simplifying.Pow
import analysis.terms.simplifying.S
import analysis.unaryMinus
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
    fun testFail1() {
        assertParsesTo("(x", x, false)
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
            val tokensAndAssignment = LexiAnalysis().parse(str)
            val parsed = SyntacticAnalysis().parse(tokensAndAssignment.first, tokensAndAssignment.second)
            assertEquals(term, parsed)
            assertTrue(success, "expected test to fail, but got results: $str -> $parsed")
            println("Parsing success on $str, simplifies to ${parsed.simplify()}")
        } catch (e: java.lang.IllegalStateException) {
            assertFalse(success)
            println("Parsing of $str failed successfully with stacktrace ${e.stackTraceToString()}")
        }
    }
}