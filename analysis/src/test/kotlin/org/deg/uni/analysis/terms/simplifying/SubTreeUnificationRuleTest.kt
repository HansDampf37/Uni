package org.deg.uni.analysis.terms.simplifying

import org.deg.uni.analysis.terms.model.Log
import org.deg.uni.analysis.terms.model.Num
import org.deg.uni.analysis.terms.model.Term
import org.deg.uni.analysis.terms.model.toTerm
import org.deg.uni.analysis.terms.one
import org.deg.uni.analysis.terms.x
import org.deg.uni.analysis.terms.y
import org.deg.uni.unification.SubTreeUnificationRule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SubTreeUnificationRuleTest {
    @Test
    fun testSubTreeUnification1() {
        val term = Log(P(one, x), P(y, one))
        val rule = Rule(P(f1, one)) { f1 }
        val subTreeRule = SubTreeUnificationRule(rule)
        assertRuleApplied(subTreeRule, term, Log(x, y))
        println(term)
    }

    @Test
    fun testSubTreeUnification2() {
        val term = "x^0 * -1".toTerm()
        val rule = Rule(S(n1, n2)) {
            if (n1.subtree != null && n2.subtree != null) (n1.subtree as Num).plus(n2.subtree as Num)
            else S(n1, n2)
        }
        val subTreeRule = SubTreeUnificationRule(rule)
        assertRuleApplied(subTreeRule, term, term, false)
        println(term)
    }

    private fun assertRuleApplied(rule: SubTreeUnificationRule<Term>, initial: Term, expectedResult: Term, successExpected: Boolean = true) {
        val applicable = rule.applicable(initial)
        println(if (applicable) "$rule is applicable to $initial" else "$rule is not applicable to $initial")
        Assertions.assertTrue(applicable == successExpected)
        if (!successExpected) return
        val apply = rule.apply(initial)
        println("replace: $initial         (${initial.quality()}) \nby:      $apply         (${apply.element().quality()})")
        Assertions.assertEquals(expectedResult, apply)
    }
}