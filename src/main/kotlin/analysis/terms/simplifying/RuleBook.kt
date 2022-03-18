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
private val n1 = UnificationVariable("n1", constraint = { it is Num })
private val n2 = UnificationVariable("n2", constraint = { it is Num })
private val n3 = UnificationVariable("n3", constraint = { it is Num })
private val f = UnificationVariable("f", filler = true)
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
        Rule(S(n1, n2, f), n1, n2, f) { S((n1.t as Num) + (n2.t as Num), f) },
        Rule(S(f, zero), f) { f },
        Rule(S(a, P(-one, a)), a) { zero },
        Rule(S(a, P(-one, a), f), f) { f },
        Rule(S(P(a, b), P(a, c)), a, b, c) { P(a, S(b, c)) },
        Rule(S(P(a, b), P(a, c), f), a, b, c, f) { Sum(P(a, S(b, c)), f) },
        Rule(S(L(c, a), L(c, b)), a, b, c) { L(c, P(a, b)) },
        Rule(S(L(c, a), L(c, b), f), a, b, c, f) { S(L(c, P(a, b)), f) },
        Rule(S(L(c, a), P(-one, L(c, b))), a, b, c) { L(c, P(a, b.inverseMult())) },
        Rule(S(L(c, a), P(-one, L(c, b)), f), a, b, c, f) { S(L(c, P(a, b.inverseMult())), f) }
    )
}

object ProductRules {
    val rules = listOf(
        Rule(P(n1, n2), n1, n2) { (n1.t as Num) * (n2.t as Num) },
        Rule(P(n1, n2, f), n1, n2, f) { P((n1.t as Num) * (n2.t as Num), f) },
        Rule(P(f, one), f) { f },
        Rule(P(f, zero), f) { zero },
        Rule(P(a, Pow(a, -one)), a) { one },
        Rule(P(a, Pow(a, -one), f), a, f) { f },
        Rule(P(a, Pow(a, b)), a, b) { Pow(a, b + one) },
        Rule(P(a, Pow(a, b), f), a, b, f) { P(Pow(a, b + one), f) },
        Rule(P(Pow(a, b), Pow(a, c)), a, b, c) { Pow(a, S(b + c)) },
        Rule(P(Pow(a, b), Pow(a, c), f), a, b, c, f) { P(Pow(a, S(b + c)), f) },
        Rule(P(Pow(a, b), Pow(c, b)), a, b, c) { Pow(S(a, c), b) },
        Rule(P(Pow(a, b), Pow(c, b), f), a, b, c, f) { P(Pow(S(a, c), b), f) },
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