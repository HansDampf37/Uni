package org.deg.uni.analysis.functions

import org.deg.uni.analysis.terms.model.simplify
import org.deg.uni.analysis.terms.model.toTerm
import org.deg.uni.analysis.terms.one
import org.deg.uni.analysis.terms.simplifying.*
import org.deg.uni.analysis.terms.x
import org.deg.uni.analysis.terms.y
import org.deg.uni.analysis.unaryMinus
import org.deg.uni.graphs.datastructures.DFS
import org.deg.uni.unification.Unifiable
import org.deg.uni.unification.UnificationRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class EquationTest {
    @Test
    fun test1() {
        val tree = Equation(one + x, one - y).toTree()
        assertEquals(9, DFS(tree).toList().size)
    }

    @Test
    fun testUnification1() {
        val e = Equation("3x + 2".toTerm(), "5".toTerm())
        val r = UnificationRule<Unifiable>(Equation(S(P(n1, v1), n2), n3)) { Equation(v1, P(S(n3, -n2), n1.inverseMult())) }
        assertTrue(r.applicable(e))
        assertEquals(Equation("x".toTerm(), "(5 + 2 * -1) * 3^-1".toTerm()), r.apply(e))
        val e1 = Equation("3x + 2".toTerm(), "5".toTerm())
        println(e1)
        val e2 = r.apply(e1) as Equation
        println(e2)
        e2.left = e2.left.simplify()
        e2.right = e2.right.simplify()
        println(e2)
    }
}