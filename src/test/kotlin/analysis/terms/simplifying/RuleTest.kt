package analysis.terms.simplifying

import analysis.terms.*
import analysis.unaryMinus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RuleTest {

    @Test
    fun testApplyRule1() {
        val rule = Rule(S(a, P(-one, a), f1)) { f1 }
        val initial = S(x, P(-one, x), y)
        assertRuleApplied(rule, initial, y)
    }

    @Test
    fun testApplyRule2() {
        val r1 = Rule(S(L(a, b), L(a, c))) { L(a, P(b, c)) }
        val t1: Term = S(L(two, x), L(two, x))
        val expected = L(two, P(x, x))
        assertRuleApplied(r1, t1, expected)

    }

    @Test
    fun testApplyRule2_1() {
        val r1 = Rule(S(L(c, a), L(c, b))) { L(c, P(a, b)) }
        val t1: Term = S(L(two, x), L(two, P(x, two)))
        val expected = L(two, P(x, P(x, two)))
        assertRuleApplied(r1, t1, expected)
    }

    @Test
    fun testApplyRule2_2() {
        val r1 = Rule(P(product1, f2)) {
            P().apply {
                if (product1.t != null && f2.t != null) {
                    addAll(product1.t as P)
                    // add(f2)
                    if (f2.t!! is P) addAll(f2.t as P) else add(f2)
                } else {
                    add(product1)
                    add(f2)
                }
            }
        }
        val t1: Term = P(x, P(x, two))
        val expected = P(x, two, x)
        assertRuleApplied(r1, t1, expected)

    }

    @Test
    fun testApplyRule2_3() {
        val r1 = Rule(P(a, a, f1)) { P(Pow(a, two), f1) }
        val t1: Term = P(x, two, x)
        val expected = P(Pow(x, two), two)
        assertRuleApplied(r1, t1, expected)
    }

    @Test
    fun testApplyRule2_4() {
        val r1 = Rule(L(a, Pow(b, c))) { P(c, L(a, b)) }
        val t1: Term = L(two, Pow(x, two))
        val expected = P(two, L(two, x))
        assertRuleApplied(r1, t1, expected)
    }

    @Test
    fun testApplyRule2_5() {
        val r1 = Rule(S(a, a)) { P(two, a) }
        val t1: Term = S(L(two, x), L(two, x))
        val expected = P(two, L(two, x))
        assertRuleApplied(r1, t1, expected)
    }

    @Test
    fun testApplyRule3() {
        val rule = Rule(S(a, P(-one, a), f1)) { f1 }
        val term = S(x, P(-one, x))
        assertRuleApplied(rule, term, term, false)
    }

    @Test
    fun testSumRules4() {
        val t = S(one, y, two)
        val rule = Rule(S(n1, n2, f1)) {
            if (n1.t != null && n2.t != null) S((n1.t as Num) + (n2.t as Num), f1) else S(
                n1,
                n2,
                f1
            )
        }
        val expected = three + y
        assertRuleApplied(rule, t, expected)
    }

    @Test
    fun testSumRules5_1() {
        val t = Sum(x, y, Product(x, Num(-4)))
        val rule = Rule(S(P(a, b), a, f1)) { S(P(a, S(b, one)), f1) }
        val expected = S(P(x, S(-four, one)), y)
        assertRuleApplied(rule, t, expected)
    }

    @Test
    fun testSumRules5_2() {
        val t = Sum(-four, one)
        val rule = Rule(S(n1, n2)) { if (n1.t != null && n2.t != null) (n1.t as Num) + (n2.t as Num) else S(n1, n2)}
        val expected = -three
        assertRuleApplied(rule, t, expected)
    }

    @Test
    fun testLogRules() {
        val t = Ln(E)
        val rule = Rule(L(a, a)) { one }
        assertRuleApplied(rule, t, one)
    }

    @Test
    fun testPowRules() {
        val t = Pow(two, S(x, one))
        val rule = Rule(Pow(a, S(b, f1))) { P(Pow(a, b), Pow(a, f1)) }
        assertRuleApplied(rule, t, P(Pow(two, x), Pow(two, one)))
    }

    fun assertRuleApplied(rule: Rule, initial: Term, expectedResult: Term, successExpected: Boolean = true) {
        val applicable = rule.applicable(initial)
        println(if (applicable.first) "$rule is applicable to $initial: ${applicable.second}" else "$rule is not applicable to $initial")
        assertTrue(applicable.first == successExpected)
        if (!successExpected) return
        val apply = rule.apply(initial)
        println("replace: $initial         (${initial.quality()}) \nby:      $apply         (${apply.quality()})")
        assertEquals(expectedResult, apply)
    }

    @Test
    fun getLogRules() {
        println(RuleBook.logRules.joinToString(separator = "\n"))
    }

    @Test
    fun getSumRules() {
        println(RuleBook.sumRules.joinToString(separator = "\n"))
    }

    @Test
    fun getProductRules() {
        println(RuleBook.productRules.joinToString(separator = "\n"))
    }

    @Test
    fun getPowerRules() {
        println(RuleBook.powerRules.joinToString(separator = "\n"))
    }

    @Test
    fun getFlattenRules() {
        println(RuleBook.flattenRules.joinToString(separator = "\n"))
    }

    @Test
    fun getNumericalRules() {
        println(RuleBook.numericalRules.joinToString(separator = "\n"))
    }

    @Test
    fun getSimplificationRules() {
        println(RuleBook.simplificationRules.joinToString(separator = "\n"))
    }
}