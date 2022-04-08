package analysis.terms.simplifying

import analysis.terms.model.*
import analysis.terms.one
import analysis.terms.two
import analysis.terms.zero
import analysis.unaryMinus
import propa.SubTreeUnificationRule
import propa.UnificationRule

typealias Rule = UnificationRule<Term>
typealias P = Product
typealias S = Sum
typealias Pow = Power
typealias L = Log

val a = UnificationVariable("a")
val b = UnificationVariable("b")
val c = UnificationVariable("c")
val d = UnificationVariable("d")
val n1 = UnificationVariable("n1", constraint = { it is Num })
val n2 = UnificationVariable("n2", constraint = { it is Num })
val n3 = UnificationVariable("n3", constraint = { it is Num })
val v1 = UnificationVariable("v1", constraint = { it is Variable })
val v2 = UnificationVariable("v2", constraint = { it is Variable })
val v3 = UnificationVariable("v3", constraint = { it is Variable })
val product1 = UnificationVariable("Product1", constraint = { it is Product })
val sum1 = UnificationVariable("Sum1", constraint = { it is Sum })
val u = UnificationVariable("u", constraint = { it is UnificationVariable })
val f1 = UnificationVariable("f1", filler = true)
val f2 = UnificationVariable("f2", filler = true)
val f3 = UnificationVariable("f3", filler = true)

class RuleBook {
    companion object {
        val logRules = listOf(
            Rule(L(a, one)) { Num(0) },
            Rule(L(a, a)) { one },
            Rule(L(a, Pow(a, b))) { b },
            Rule(L(a, Pow(b, c))) { P(c, L(a, b)) },
            Rule(L(c, P(a, b))) { S(L(c, a), L(c, b)) }
        )

        val sumRules = listOf(
            Rule(S(f1, zero)) { f1 },
            Rule(S(a, P(-one, a))) { zero },
            Rule(S(a, P(-one, a), f2)) { f2 },
            Rule(S(a, a)) { P(two, a) },
            Rule(S(a, a, f1)) { S(P(two, a), f1) },

            Rule(S(P(a, f1), P(a, f2))) { P(a, S(f1, f2)) },
            Rule(S(P(a, f1), P(a, f2), f3)) { S(P(a, S(f1, f2)), f3) },
            Rule(S(P(a, f1), a)) { P(a, S(f1, one)) },
            Rule(S(P(a, f1), a, f2)) { S(P(a, S(f1, one)), f2) },

            Rule(S(P(a, f1), P(Pow(a, b), f2))) { P(a, S(f1, P(f2, Pow(a, S(b, -one))))) },
            Rule(S(P(a, f1), P(Pow(a, b), f2), f3)) { S(P(a, S(f1, P(f2, Pow(a, S(b, -one))))), f3) },

            Rule(S(L(c, a), L(c, b))) { L(c, P(a, b)) },
            Rule(S(L(c, a), L(c, b), f1)) { S(L(c, P(a, b)), f1) },
            Rule(S(L(c, a), P(-one, L(c, b)))) { L(c, P(a, b.inverseMult())) },
            Rule(S(L(c, a), P(-one, L(c, b)), f1)) { S(L(c, P(a, b.inverseMult())), f1) },
        )

        val productRules = listOf(
            Rule(P(f1, one)) { f1 },
            Rule(P(f1, zero)) { zero },
            Rule(P(f1, Pow(f1, -one))) { one },
            Rule(P(a, Pow(a, -one), f1)) { f1 },
            Rule(P(f1, S(a, f2))) { S(P(f1, a), P(f1, f2)) },

            Rule(P(a, a)) { Pow(a, two) },
            Rule(P(a, a, f1)) { P(Pow(a, two), f1) },
            Rule(P(f1, Pow(f1, b))) { Pow(f1, S(b, one)) },
            Rule(P(a, Pow(a, b), f1)) { P(Pow(a, S(b, one)), f1) },

            Rule(P(Pow(a, b), Pow(a, c))) { Pow(a, S(b, c)) },
            Rule(P(Pow(a, b), Pow(a, c), f1)) { P(Pow(a, S(b, c)), f1) },
            Rule(P(Pow(a, b), Pow(c, b))) { Pow(P(a, c), b) },
            Rule(P(Pow(a, b), Pow(c, b), f1)) { P(Pow(P(a, c), b), f1) },
        )

        val powerRules = listOf(
            Rule(Pow(a, one)) { a },
            Rule(Pow(a, zero)) { one },
            Rule(Pow(Pow(a, b), c)) { Pow(a, P(b, c)) },
            Rule(Pow(a, Pow(a, b))) { Pow(a, S(b, one)) },
            Rule(P(Pow(a, b), Pow(a, c))) { Pow(a, S(b, c)) },
            Rule(P(Pow(a, b), Pow(c, b))) { Pow(S(a, c), b) },
            Rule(Pow(a, P(L(a, b), c))) { Pow(b, c) },
            Rule(Pow(a, L(a, b))) { b },
            Rule(Pow(a, L(a, b))) { b },
            Rule(Pow(a, S(b, f1))) { P(Pow(a, b), Pow(a, f1)) }
        )

        val flattenRules = listOf(
            Rule(S(a)) { a },
            Rule(S(sum1, f2)) {
                S().apply {
                    if (sum1.subtree != null && f2.subtree != null) {
                        addAll(sum1.subtree as S)
                        if (f2.subtree!! is S) addAll(f2.subtree as S) else addNode(f2)
                    } else {
                        addNode(sum1)
                        addNode(f2)
                    }
                }
            },
            Rule(P(a)) { a },
            Rule(P(product1, f2)) {
                P().apply {
                    if (product1.subtree != null && f2.subtree != null) {
                        addAll(product1.subtree as P)
                        if (f2.subtree!! is P) addAll(f2.subtree as P) else addNode(f2)
                    } else {
                        addNode(product1)
                        addNode(f2)
                    }
                }
            }
        )

        val numericalRules = listOf(
            Rule(S(n1, n2)) {
                if (n1.subtree != null && n2.subtree != null) (n1.subtree as Num).plus(n2.subtree as Num)
                else S(n1, n2)
            },
            Rule(S(n1, n2, f1)) {
                if (n1.subtree != null && n2.subtree != null) S((n1.subtree as Num).plus(n2.subtree as Num), f1)
                else S(n1, n2, f1)
            },
            Rule(P(n1, n2)) {
                if (n1.subtree != null && n2.subtree != null) (n1.subtree as Num).times(n2.subtree as Num)
                else P(n1, n2)
            },
            Rule(P(n1, n2, f1)) {
                if (n1.subtree != null && n2.subtree != null) P((n1.subtree as Num).times(n2.subtree as Num), f1)
                else P(n1, n2, f1)
            },
            Rule(Pow(n1, n2)) {
                if (n1.subtree != null && n2.subtree != null) (n1.subtree as Num).pow(n2.subtree as Num)
                else Pow(n1, n2)
            },
            Rule(Pow(n1, P(n2, f1))) {
                if (n1.subtree != null && n2.subtree != null) Pow((n1.subtree as Num).pow(n2.subtree as Num), f1)
                else Pow(n1, P(n2, f1))
            },
            Rule(Pow(P(n1, f1), n2)) {
                if (n1.subtree != null && n2.subtree != null) P((n1.subtree as Num).pow(n2.subtree as Num), Pow(f1, n2))
                else Pow(P(n1, f1), n2)
            },
            Rule(Log(n1, n2)) {
                if (n1.subtree != null && n2.subtree != null) (n1.subtree as Num).log(n2.subtree as Num)
                else Log(n1, n2)
            },
            Rule(P(n1, Pow(n2, -one))) {
                if (n1.subtree != null && n2.subtree != null) (n1.subtree as Num).times((n2.subtree as Num).inverseMult())
                else P(n1, Pow(n2, -one))
            }
        )

        val simplificationRules = listOf(logRules, sumRules, productRules, powerRules).flatten()
        private val componentSimplificationRules =
            listOf(simplificationRules, flattenRules, numericalRules).flatten().map { SubTreeUnificationRule(it) }
        val rules = listOf(simplificationRules, flattenRules, numericalRules, componentSimplificationRules).flatten()
    }
}

fun main() {
    println(RuleBook.rules.joinToString("\n"))
}