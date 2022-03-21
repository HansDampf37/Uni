package analysis.terms.simplifying

import analysis.terms.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RuleBookTest {
    @Test
    fun testSumRules() {
        val term = Sum(Log(x, y), Log(x, four))
        val result = Log(x, Product(y, four))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules1() {
        val term = Sum(one, two)
        val result = three
        val rules = RuleBook.numericalRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules2() {
        val term = Sum(one, two, x)
        val result = three + x
        val rules = RuleBook.numericalRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules3() {
        val term = Sum(one, y, two)
        val result = three + y
        val rules = RuleBook.numericalRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testLogRules() {
        val term = Log(Sum(one, x), one)
        val result = zero
        val rules = RuleBook.logRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testLogRules2() {
        val term = Log(x, Power(x, y))
        val result = y
        val rules = RuleBook.logRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testLogRules3() {
        val term = Log(Sum(x, one), Power(Sum(x, one), y))
        val result = y
        val rules = RuleBook.logRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testLogRules4() {
        val term = Log(Sum(x, one), Power(Sum(one, x), y))
        val result = y
        val rules = RuleBook.logRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testPowerRules1() {
        val term = Pow(five, two)
        val result = Num(25)
        val rules = RuleBook.numericalRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testPowerRules2() {
        val term = Pow(five, Num(1, 2))
        val rules = RuleBook.numericalRules
        assertCanBeSimplified(rules, term, term)
    }

    @Test
    fun testPowerRules3() {
        val term = Pow(five, S(x, y))
        val expected = P(Pow(five, x), Pow(five, y))
        val rules = RuleBook.simplificationRules
        assertCanBeSimplified(rules, term, expected)
    }

    @Test
    fun testLogRules1() {
        val term = Ln(E)
        val rules = RuleBook.logRules
        assertCanBeSimplified(rules, term, one)
    }

    private fun assertCanBeSimplified(
        rules: List<Rule>,
        term: Term,
        result: Term
    ) {
        Assertions.assertTrue(rules.filter { it.applicable(term).first }.map { it.apply(term) }
            .contains(result), "Expected simplifcation from $term to $result")
    }
}