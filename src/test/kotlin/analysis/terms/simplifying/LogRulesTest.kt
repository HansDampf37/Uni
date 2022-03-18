package analysis.terms.simplifying

import analysis.terms.Log
import analysis.terms.Power
import analysis.terms.Sum
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class LogRulesTest {
    @Test
    fun testLogRules() {
        val t = Log(Sum(one, x), one)
        Assertions.assertEquals(
            zero,
            LogRules.rules.filter { it.applicable(t).first }.map { it.apply(t) }[0]
        )
    }

    @Test
    fun testLogRules2() {
        val t = Log(x, Power(x, y))
        Assertions.assertEquals(
            y,
            LogRules.rules.filter { it.applicable(t).first }.map { it.apply(t) }[0]
        )
    }

    @Test
    fun testLogRules3() {
        val t = Log(Sum(x, one), Power(Sum(x, one), y))
        Assertions.assertTrue(LogRules.rules.filter { it.applicable(t).first }.map { it.apply(t) }
            .contains(y))
    }

    @Test
    fun testLogRules4() {
        val t = Log(Sum(x, one), Power(Sum(one, x), y))
        Assertions.assertTrue(
            LogRules.rules.filter { it.applicable(t).first }.map { it.apply(t) }.contains(y))
    }
}