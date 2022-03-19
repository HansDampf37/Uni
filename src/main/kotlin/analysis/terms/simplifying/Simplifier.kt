package analysis.terms.simplifying

import algo.datastructures.DFS
import analysis.terms.*
import kotlin.math.sqrt

fun Term.quality(): Double {
    if (isLeaf()) return 0.0
    val allNodes = DFS(this).toList()
    val amountOfVariables = allNodes.filterIsInstance<Variable>().size
    val weightDepth = 0.2
    val weightSize = 0.2
    val weightAmountOfVariable = 0.4
    val weightAmountOfLeaves = 1 - weightDepth - weightSize - weightAmountOfVariable
    val punishment = (weightDepth * depth() + weightSize * nodeSize() + weightAmountOfLeaves * width() + amountOfVariables * weightAmountOfVariable)
    return -sqrt(punishment)
}

class Simplifier {
    var N: Int = -1
    var K: Int = -1

    fun simplify(t: Term): Term {
        val quality = 9 * t.quality()
        N = -quality.toInt()
        K = 8

        var l = listOf(Pair(t, t.quality()))
        var best = Pair(t, quality)
        repeat(N) {
            l.forEach { simplifyComponents(it.first) }
            var l1 = simplify(l, RuleBook.numericalRules)
            l = l1.ifEmpty { l }
            l1 = simplify(l, RuleBook.simplificationRules)
            l = l1.ifEmpty { l }
            l1 = simplify(l, RuleBook.flattenRules)
            l = l1.ifEmpty { l }
            if (l.isEmpty()) return best.first
            if (l[0].second > best.second) {
                best = l[0]
            }
        }
        return best.first
    }

    private fun simplifyComponents(t: Term) {
        for (i in 0 until t.nodeSize()) t.setNode(i, t.getNode(i).get().simplify())
    }

    fun flatten(t: Term): Term {
        if (t.isLeaf()) return t
        else {
            for (i in 0 until t.nodeSize()) flatten(t.getNode(i).get())
            for (i in 0 until t.nodeSize()) {
                for (rule in RuleBook.flattenRules) {
                    if (rule.applicable(t.getNode(i).get()).first) {
                        t.setNode(i, rule.apply(t.getNode(i).get()))
                    }
                }
            }
        }
        return t
    }

    private fun simplify(l: List<Pair<Term, Double>>, rules: List<Rule>): List<Pair<Term, Double>> {
        val res = l.map { term -> rules.filter {it.applicable(term.first).first}.map { rule -> rule.apply(term.first) } }.flatten()
        return res.map { Pair(it, it.quality()) }.sortedByDescending { it.second }.slice(0 until minOf(K, res.size))
    }
}
/*

abstract class Simplifier<T : Term>(val rules: List<Rule>) {
    */
/**
 * Simplifies the given expression
 *
 * @param t the Term
 * @return simplified version
 *//*

    open fun simplify(t: T): Term {
        pullUp(t)
        var res: Term
        res = t.eval()
        while (true) {
            try {
                res = rules.first { it.applicable(res).first }.apply(res)
            } catch (e: NoSuchElementException) {
                break
            }
        }
        return res
    }

    */
/**
 * Combines numbers in term t sum(3, 2, x) -> sum(5, x)
 *
 * @param t
 * @return
 *//*

    abstract fun eval(t: T): Term

    */
/**
 * Flattens all layers in this term not just one
 *
 * @param t
 *//*

    abstract fun pullUp(t: T): Term
}


class SimplifierTrivial<T : Term> : Simplifier<T>(listOf()) {
    override fun simplify(t: T) = t
    override fun eval(t: T) = t
    override fun pullUp(t: T) = t
}

class SumSimplifier : Simplifier<Sum>(RuleBook.sumRules) {
    override fun eval(t: Sum): Sum {
        var n = Num(0)
        val res = t.clone()
        for (el in res) if (el is Num) n += el
        res.removeIf { it is Num }
        if (res.isEmpty() || n != Num(0)) res.add(n)
        return res
    }

    override fun pullUp(t: Sum): Term {
        if (t.size == 1) t[0].pullUp()
        for (i in t.indices.reversed()) {
            val el = t[i]
            el.pullUp()
            if (el is Sum) {
                t.removeAt(i)
                t.addAll(el)
            } else if (el is TermContainer) {
                val v = el.value
                if (v != null) {
                    t.removeAt(i)
                    t.add(v)
                }
            }
        }
        return t
    }

    fun factorize(sum: Sum): Sum {
        if (sum.size <= 1) return sum
        for (a in sum) {
            if (a == Num(1) || a == Num(-1) || a == Num(0)) continue
            val divAArray = Array(sum.size) { j ->
                val q = (sum[j] / a).simplify()
                if (q is Num) q else null
            }
            if (divAArray.count { it != null } <= 1) continue
            for (j in sum.indices.reversed()) if (divAArray[j] != null) sum.removeAt(j)
            sum.add(Product(a, Sum(divAArray.filterNotNull())))
            return factorize(sum)
        }
        // could not be factorized
        return sum
    }
}

class ProductSimplifier : Simplifier<Product>(RuleBook.productRules) {
    override fun pullUp(t: Product): Term {
        if (t.size == 1) t[0].pullUp()
        for (i in t.indices.reversed()) {
            val el = t[i]
            el.pullUp()
            if (el is Product) {
                t.removeAt(i)
                t.addAll(el)
            } else if (el is TermContainer) {
                val v = el.value
                if (v != null) {
                    t.removeAt(i)
                    t.add(v)
                }
            }
        }
        return t
    }

    override fun eval(t: Product): Product {
        var n = Num(1)
        val res = t.clone()
        for (el in res) if (el is Num) n *= el
        res.removeIf { it is Num }
        if (res.isEmpty() || n != Num(1)) res.add(n)
        return res
    }

    fun potentiate(pr: Product): Product {
        for (a in pr) {
            val a1 = if (a !is Power) Power(a, Num(1)) else a
            val logABaseArray = Array(pr.size) { j ->
                val other = if (pr[j] !is Power) Power(pr[j], Num(1)) else pr[j] as Power
                if (a1.base == other.base) other.exponent else null
            }
            if (logABaseArray.count { it != null } <= 1) continue
            for (j in pr.indices.reversed()) if (logABaseArray[j] != null) pr.removeAt(j)
            pr.add(Power(a1.base, Sum(logABaseArray.filterNotNull())))
            return potentiate(pr)
        }
        // could not be potentiate
        return pr
    }
}

class PowerSimplifier : Simplifier<Power>(RuleBook.powerRules) {
    override fun pullUp(t: Power): Term {
        t.base = t.base.pullUp()
        t.exponent = t.exponent.pullUp()
        return t
    }

    override fun eval(t: Power): Term {
        return if (t.base is Num && t.exponent is Num) {
            val numerator = (t.base as Num).num.pow(t.exponent.toDouble())
            val denominator = (t.base as Num).denominator.pow(t.exponent.toDouble())
            val lengths = Pair(numerator.toString().split(".")[1].length, denominator.toString().split(".")[1].length)
            if (lengths.first < 5 && lengths.second < 5) {
                Num(numerator, denominator)
            } else {
                t
            }
        } else {
            t
        }
    }
}

class LogSimplifier : Simplifier<Log>(RuleBook.logRules) {
    override fun eval(t: Log): Term {
        return if (t.base is Num && t.arg is Num) {
            val x = log((t.arg as Num).num, (t.base as Num).toDouble())
            val y = log((t.arg as Num).denominator, (t.base as Num).toDouble())
            val length = (x - y).toString().split(".")[1].length
            if (length < 5) {
                Num(x - y)
            } else {
                t
            }
        } else {
            t
        }
    }
    override fun pullUp(t: Log): Term {
        t.base = t.base.pullUp()
        t.arg = t.arg.pullUp()
        return t
    }
}

class VariableSimplifier : Simplifier<Variable>(listOf()) {

    override fun simplify(t: Variable): Term = t.value ?: t
    override fun eval(t: Variable): Term = t.value ?: t
    override fun pullUp(t: Variable): Term = t.value?.pullUp() ?: t
}
*/
