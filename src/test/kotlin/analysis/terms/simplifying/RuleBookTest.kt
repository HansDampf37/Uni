package analysis.terms.simplifying

import analysis.terms.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RuleBookTest {

    @Test
    fun simplifyExplicitly() {
        val x = Variable("x")
        //val term = Sum(Log(Num(3), x), Log(Num(3), x))
        val term = Sum()
        term.add(Sum(Num(3), Num(4)))
        val uni = Sum().apply { add(Sum(f1)) }
        println(uni)
        println(term)
        println(Rule(uni, f1) { Sum(f1, f2) }.applicable(term))
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
        println(RuleBook.simplificationRules.joinToString())
    }
}