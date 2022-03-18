package analysis.terms.simplifying

import analysis.terms.Log
import analysis.terms.Product
import analysis.terms.Sum
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SumRulesTest {
    @Test
    fun testSumRules() {
        val t = Sum(Log(x, y), Log(x, four))
        Assertions.assertEquals(
            Log(x, Product(y, four)),
            SumRules.rules.filter { it.applicable(t).first }.map { it.apply(t) }[0]
        )
    }

    @Test
    fun testSumRules1() {
        val t = Sum(one, two)
        Assertions.assertEquals(
            three,
            SumRules.rules.filter { it.applicable(t).first }.map { it.apply(t) }[0]
        )
    }

    @Test
    fun testSumRules2() {
        val t = Sum(one, two, x)
        Assertions.assertEquals(
            three + x,
            SumRules.rules.filter { it.applicable(t).first }.map { it.apply(t) }[0]
        )
    }

    @Test
    fun testSumRules3() {
        val t = Sum(one, y, two)
        Assertions.assertEquals(
            three + y,
            SumRules.rules.filter { it.applicable(t).first }.map { it.apply(t) }[0]
        )
    }
}