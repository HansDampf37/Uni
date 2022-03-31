package analysis.terms.simplifying

import analysis.terms.*
import analysis.unaryMinus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RuleBookTest {
    @Test
    fun testSumRules1() {
        val term = Sum(x, y, zero)
        val result = Sum(x, y)
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules2() {
        val term = Sum(x, P(-one, x))
        val result = zero
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules3() {
        val term = Sum(x, P(-one, x), y)
        val result = y
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules4() {
        val term = Sum(x, x)
        val result = P(two, x)
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules5() {
        val term = Sum(x, x, y)
        val result = S(P(two, x), y)
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules6() {
        val term = S(P(x, z, S(y, one)), P(x, y, S(z, one)))
        val result = P(x, S(P(z, S(y, one)), P(y, S(z, one))))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules7() {
        val term = S(P(x, z, S(y, one)), P(x, y, S(z, one)), q, one)
        val result = S(P(x, S(P(z, S(y, one)), P(y, S(z, one)))), S(q, one))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules8() {
        val term = S(P(x, S(z, one)), x)
        val result = P(x, S(S(z, one), one))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules9() {
        val term = S(P(x, S(z, one)), x, S(y, one))
        val result = S(P(x, S(S(z, one), one)), S(y, one))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules10() {
        val term = S(P(y, x, z), P(two, Pow(x, two), three))
        val result = P(x, S(P(y, z), P(P(two, three), Pow(x, S(two, -one)))))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules10_1() {
        val term = S(P(Ln(two), Pow(two, x), Pow(x, two)), P(Pow(two, x), two, x))
        val expected = P(Pow(two, x), S(P(Ln(two), Pow(x, two)), P(two, x)))
        assertCanBeSimplified(RuleBook.simplificationRules, term, expected)
    }

    @Test
    fun testSumRules11() {
        val term = S(P(y, x, z), P(two, Pow(x, two), three), q, two)
        val result = S(P(x, S(P(y, z), P(P(two, three), Pow(x, S(two, -one))))), S(q, two))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules12() {
        val term = S(Log(x, y), Log(x, four))
        val result = Log(x, P(y, four))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules13() {
        val term = S(Log(x, y), Log(x, four), two, x)
        val result = S(Log(x, P(y, four)), S(two, x))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules14() {
        val term = S(L(x, y), P(-one, L(x, two)))
        val result = L(x, P(y, Pow(two, -one)))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testSumRules15() {
        val term = S(L(x, y), P(-one, L(x, two)), q, one)
        val result = S(L(x, P(y, Pow(two, -one))), S(q, one))
        val rules = RuleBook.sumRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules1() {
        val term = P(x, y, one, z)
        val result = P(x, y, z)
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules2() {
        val term = P(x, y, zero, z)
        val result = zero
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules3() {
        val term = P(x, y, Pow(P(x, y), -one))
        val result = one
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules4() {
        val term = P(x, Pow(x, -one), z, q)
        val result = P(z, q)
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules5() {
        val term = P(x, y, S(z, one, q))
        val result = S(P(P(x, y), z), P(P(x, y), S(one, q)))
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules6() {
        val term = P(S(x, two), S(two, x))
        val result = Pow(S(x, two), two)
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules7() {
        val term = P(S(x, two), S(two, x), q, two)
        val result = P(Pow(S(x, two), two), P(q, two))
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules8() {
        val term = P(two, x, y, Pow(P(two, x, y), S(one, x)))
        val result = Pow(P(two, x, y), S(S(one, x), one))
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules9() {
        val term = P(x, Pow(x, S(two, Ln(y))), q)
        val result =  P(Pow(x, S(one, S(two, Ln(y)))), q)
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules10() {
        val term = P(Pow(Ln(x), y), Pow(Ln(x), z))
        val result =  Pow(Ln(x), S(y, z))
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules11() {
        val term = P(Pow(Ln(x), y), Pow(Ln(x), z), q)
        val result =  P(Pow(Ln(x), S(y, z)), q)
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules12() {
        val term = P(Pow(y, Ln(x)), Pow(z, Ln(x)))
        val result =  Pow(P(y, z), Ln(x))
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testProductRules13() {
        val term = P(Pow(y, Ln(x)), Pow(z, Ln(x)), q)
        val result =  P(Pow(P(y, z), Ln(x)), q)
        val rules = RuleBook.productRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testNumericalRules() {
        val term = Sum(one, y, two)
        val result = three + y
        val rules = RuleBook.numericalRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testNumericalRules1() {
        val term = Sum(one, two)
        val result = three
        val rules = RuleBook.numericalRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testNumericalRules2() {
        val term = Sum(one, two, x)
        val result = three + x
        val rules = RuleBook.numericalRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testNumericalRules3() {
        val term = Sum(one, y, two)
        val result = three + y
        val rules = RuleBook.numericalRules
        assertCanBeSimplified(rules, term, result)
    }

    @Test
    fun testNumericalRules4() {
        println(Pow(one, three).simplify())
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

    /*@Test
    fun testPowerRules4() {
        val term = Pow(two, x)
        val rules = RuleBook.rules
        assertCanBeSimplified(rules, term, P(two, Pow(two, S(x, -one))))
    }*/

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
        println("Term: $term")
        Assertions.assertTrue(rules.filter {
            val applicable = it.applicable(term)
            if (applicable.first) println("with: $it  (${applicable.second})")
            applicable.first
        }.map {
            val res = it.apply(term)
            println("is:   $res")
            res
        }
            .contains(result), "Expected simplification from $term to $result")
    }
}