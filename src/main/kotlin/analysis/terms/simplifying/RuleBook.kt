package analysis.terms.simplifying

import analysis.inverseMult
import analysis.terms.*
import analysis.unaryMinus

typealias Rule = SimplificationRule
typealias P = Product
typealias S = Sum
typealias Pow = Power
typealias L = Log

private val a = UnificationVariable("a")
private val b = UnificationVariable("b")
private val c = UnificationVariable("c")
private val d = UnificationVariable("d")
private val n1 = UnificationVariable("n1", { it is Num }, null)
private val n2 = UnificationVariable("n2", { it is Num }, null)
private val n3 = UnificationVariable("n3", { it is Num }, null)
val zero = Num(0)
val one = Num(1)
val two = Num(2)
val three = Num(3)
val four = Num(4)
val five = Num(5)
val six = Num(6)
val seven = Num(7)
val x = Variable("x")
val y = Variable("y")
val z = Variable("z")
val q = Variable("q")

object LogRules {
    val rules = listOf(
        Rule(L(a, one), a) { Num(0) },
        Rule(L(a, Pow(a, b)), a, b) { b },
        Rule(L(a, Pow(b, c)), a, b, c) { P(c, L(a, b)) },
    )
}

object SumRules {
    val rules = listOf(
        Rule(S(n1, n2), n1, n2) { (n1.t as Num) + (n2.t as Num) },
        Rule(S(n1, n2, a), n1, n2, a) { S((n1.t as Num) + (n2.t as Num), a) },
        Rule(S(a, zero), a) { a },
        Rule(S(a, P(-one, a)), a) { zero },
        Rule(S(P(a, b), P(a, c)), a, b, c) { P(a, S(b, c)) },
        Rule(S(P(a, b), P(a, c), d), a, b, c, d) { Sum(P(a, S(b, c)), d) },
        Rule(S(L(c, a), L(c, b)), a, b, c) { L(c, P(a, b)) },
        Rule(S(L(c, a), L(c, b), d), a, b, c, d) { S(L(c, P(a, b)), d) },
        Rule(S(L(c, a), P(-one, L(c, b))), a, b, c) { L(c, P(a, b.inverseMult())) },
        Rule(S(L(c, a), P(-one, L(c, b)), d), a, b, c, d) { S(L(c, P(a, b.inverseMult())), d) }
    )
}

object ProductRules {
    val rules = listOf(
        Rule(P(n1, n2), n1, n2) { (n1.t as Num) * (n2.t as Num) },
        Rule(P(n1, n2, a), n1, n2, a) { P((n1.t as Num) * (n2.t as Num), a) },
        Rule(P(a, one), a) { a },
        Rule(P(a, zero), a) { zero },
        Rule(P(a, Pow(a, -one)), a) { one },
        Rule(P(a, Pow(a, -one), b), a, b) { b },
        Rule(P(a, Pow(a, b)), a, b) { Pow(a, b + one) },
        Rule(P(a, Pow(a, b), c), a, b, c) { P(Pow(a, b + one), c) },
        Rule(P(Pow(a, b), Pow(a, c)), a, b, c) { Pow(a, S(b + c)) },
        Rule(P(Pow(a, b), Pow(a, c), d), a, b, c, d) { P(Pow(a, S(b + c)), d) },
        Rule(P(Pow(a, b), Pow(c, b)), a, b, c) { Pow(S(a, c), b) },
        Rule(P(Pow(a, b), Pow(c, b), d), a, b, c, d) { P(Pow(S(a, c), b), d) },
    )
}

object PowerRules {
    val rules = listOf(
        Rule(Pow(n1, n2), n1, n2) { (n1.t as Num).pow(n2.t as Num) },
        Rule(Pow(a, one), a) { a },
        Rule(Pow(a, zero), a) { one },
        Rule(Pow(Pow(a, b), c), a, b, c) { Pow(a, P(b, c)) },
        Rule(Pow(a, Pow(a, b)), a, b) { Pow(a, b + one) },
        Rule(P(Pow(a, b), Pow(a, c)), a, b, c) { Pow(a, S(b + c)) },
        Rule(P(Pow(a, b), Pow(c, b)), a, b, c) { Pow(S(a, c), b) },
        Rule(Pow(a, L(a, b) * c), a, b, c) { Pow(b, c) },
        Rule(Pow(a, L(a, b)), a, b, c) { b },
        Rule(Pow(a, L(a, b)), a, b) { b }
    )
}