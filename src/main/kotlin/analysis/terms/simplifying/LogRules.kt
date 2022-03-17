package analysis.terms.simplifying

import analysis.inverseMult
import analysis.terms.*

typealias Rule = SimplificationRule
typealias P = Product
typealias S = Sum
typealias Pow = Power
typealias L = Log

private val a = UnificationVariable("a")
private val b = UnificationVariable("b")
private val c = UnificationVariable("c")
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
        Rule(Pow(a, L(a, b)), a, b) { b },
        Rule(L(a, Pow(a, b)), a, b) { b },
        Rule(L(a, Pow(b, c)), a, b, c) { P(c, L(a, b)) },
        Rule(S(L(c, a), L(c, b)), a, b, c) { L(c, P(a, b)) },
        Rule(S(L(c, a), P(Num(-1), L(c, b))), a, b, c) { L(c, P(a, b.inverseMult())) },
    )
}

object SRules {
    val rules = listOf(
        // Distribute
        Rule(S(P(a, b), P(a, c))) { P(a, S(b, c)) }
    )
}

fun main() {
    println(Rule(L(a, Pow(b, c)), a, b, c) { P(c, L(a, b)) }.applyIfPossible(Log(Sum(x, one), Power(Sum(one, x), y))))
}