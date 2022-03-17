package analysis.terms

import kotlin.math.pow

interface Simplifier<T : Term> {
    /**
     * Simplifies the given expression
     *
     * @param t the Term
     * @return simplified version
     */
    fun simplify(t: T): Term

    /**
     * Replaces a trivial subterm by a more trivial subterm e.g. sum(x) -> x, prod(x) -> x, Power(x,1) -> x, Power(x, 0) -> 1
     *
     * @param t
     * @return
     */
    fun flatten(t: T): Term

    /**
     * Combines numbers in term t sum(3, 2, x) -> sum(5, x)
     *
     * @param t
     * @return
     */
    fun eval(t: T): Term
}

class SimplifierTrivial<T: Term>: Simplifier<T> {
    override fun simplify(t: T) = t
    override fun flatten(t: T) = t
    override fun eval(t: T) = t
}

class SumSimplifier: Simplifier<Sum> {
    /**
     * 2 * a + 3 - 2 * (3 * a * 1/3) - 2 - a^(1 - 1) + x -> x
     * 1. simplify components 2 * a + 3 - 2 * a - 2 - 1 + x
     * 2. eval 2 * a - 2 * a + x
     * 3. for each element != 1 != -1 try to factor out with [factorize] factor out 2 * a * (1 - 1) + x
     * 3.5 simplify components 0 + x
     * 4. eval x
     * 5. sort 1
     * 6. flatten this 1
     *
     * @param t Sum
     * @return t in a simpler form
     */
    override fun simplify(t: Sum): Term {
        var res: Term
        // 0. pull up
        pullUp(t)
        // 1. simplify components
        res = Sum(t.map { it.simplify() })
        // 2. eval
        res = eval(res)
        // 3. factorize
        res = factorize(res)
        // 3.5
        res = Sum(res.map { it.simplify() })
        // 4
        res = res.eval()
        // 5
        // 6
        //println("Simplifying $t to ${res.flatten()}")
        return res.flatten()
    }

    override fun flatten(t: Sum): Term {
        val t1 = t.map { it.flatten() }.toMutableList()
        for (i in t1.indices) {
            if (t1[i] is Sum) {
                t1.addAll(t1[i] as Sum)
                t1.removeAt(i)
            }
        }
        if (t1.size == 1) return t1[0]
        return Sum(t1)
    }

    override fun eval(t: Sum): Sum {
        var n = Num(0)
        val res = t.clone()
        for (el in res) if (el is Num) n += el
        res.removeIf { it is Num }
        if (res.isEmpty() || n != Num(0)) res.add(n)
        return res
    }

    fun pullUp(sum: Sum) {
        for (i in sum.indices.reversed()) {
            if (sum[i] is Sum) {
                pullUp(sum[i] as Sum)
                val removed = sum.removeAt(i)
                sum.addAll(removed as Sum)
            }
        }
    }

    /**
     * a + 2 * a + 3 + sqrt(3) - 2 * sqrt(3)
     * 1. take first comp (a):
     * 2. iterate over sum and create divisible array that contains the quotient q=it/a for each element if q is Num and Null otherwise
     * 3. check if divisible array contains more than 1 no null element. if not continue with 1 with next summand.
     * 4. iterate over sum from behind and remove all elements with entries != 0 in the divisible array and add their divisors to Product(a, Sum(it, it, it, ...))
     * 5. add Product to sum
     * 6. return sum.factorize()
     *
     * @return
     */
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

class ProductSimplifier : Simplifier<Product> {
    /**
     * 2 * a² * 3 * a * (1 - 1)
     * 1. simplify components 2 * a² * 3 * a * 0
     * 2. eval a² * a * 0
     * 2.5. if contains 0 return 0
     * 3. for each element != 1 try to potentiate with [potentiate] 0 * a⁽²⁺¹⁾
     * 3.5 simplify components 0 * a³
     * 4. eval 0 * a³
     * 4.5 distribute contained sums ?
     * 5. sort a³ * 0
     * 6. flatten this a³ * 0
     *
     * @param t Product
     * @return t in a simpler form
     */
    override fun simplify(t: Product): Term {
        var res: Term
        // 0. pullUp
        pullUp(t)
        // 1. simplify components
        res = Product(t.map { it.simplify() })
        // 2. evaluate
        res = eval(res)
        // 2.5. does not contain 0
        if (res.any { it == Num(0) }) return Num(0)
        // 3. potentiate
        res = potentiate(res)
        // 3.5. simplify components
        res = Product(res.map { it.simplify() })
        // 4. evaluate
        res = eval(res)
        // 4.5 distribute sums
        if (res.size >= 2) {
            for (i in res.indices.reversed()) {
                if (res[i] is Sum) {
                    val s = res.removeAt(i) as Sum
                    return Sum(s.map { it * res }).simplify()
                }
            }
        }
        // 5. sort
        // 6. flatten
        //println("Simplifying $t to ${res.flatten()}")
        return res.flatten()
    }

    fun pullUp(pr: Product) {
        for (i in pr.indices.reversed()) {
            if (pr[i] is Product) {
                pullUp(pr[i] as Product)
                val removed = pr.removeAt(i)
                pr.addAll(removed as Product)
            }
        }
    }

    override fun flatten(t: Product): Term {
        val t1 = t.map { it.flatten() }.toMutableList()
        for (i in t1.indices) {
            if (t1[i] is Product) {
                t1.addAll(t1[i] as Product)
                t1.removeAt(i)
            }
        }
        if (t1.size == 1) return t1[0]
        return Product(t1)
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

class PowerSimplifier: Simplifier<Power> {

    /**
     * (x ^ ((2 ^ b) ^ c)) ^ 2
     * 1. simplify components x ^ (2 * 2 ^ (c * b))
     * 2. if exp == 0 return 1, if base == 0 return 0, x ^ (2 * 2 ^ (c * b))
     * 3. simplify components x ^ 2 ^ (c * b + 1)
     * 4. eval x ^ 2 ^ (c * b + 1)
     * 5. flatten x ^ 2 ^ (c * b + 1)
     *
     * @param t Product
     * @return t in a simpler form
     */
    override fun simplify(t: Power): Term {
        var res: Term
        // 1. simplify components
        res = Power(t.base.simplify(), t.exponent.simplify())
        // 2. evaluate
        if (res.exponent == Num(0)) return Num(1)
        if (res.base == Num(0)) return Num(0)
        // 3. simplify components
        res = Power(res.base.simplify(), res.exponent.simplify())
        // 4. eval
        res = eval(res)
        //println("Simplifying $t to ${res.flatten()}")
        if (res !is Power) return res
        if (res.base is Product) {
            return Product((res.base as Product).map { Power(it, res.exponent) }).simplify()
        }
        // 5. flatten
        return res.flatten()
    }

    override fun flatten(t: Power): Term {
        t.exponent = t.exponent.flatten()
        t.base = t.base.flatten()
        if (t.exponent == Num(1)) return t.base
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

class VariableSimplifier: Simplifier<Variable> {

    override fun simplify(t: Variable): Term = t.value ?: t
    override fun flatten(t: Variable): Term = t
    override fun eval(t: Variable): Term = t.value ?: t
}
