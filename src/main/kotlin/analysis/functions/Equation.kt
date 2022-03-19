package analysis.functions

import analysis.terms.*

class Equation(private var left: Term, private var right: Term) {

    fun solve(): List<MutableMap<Variable, Term>> {
        left -= right
        right = Num(0)
        left = left.simplify()
        return separateVariables()
    }

    private fun separateVariables(): List<MutableMap<Variable, Term>> {
        when (left) {
            is Power -> return Equation((left as Power).base, Power(right, Num(1, 2))).solve()
            is Sum -> {
                for (el in (left as Sum).indices.reversed()) {
                    try {
                        (left as Sum)[el].toDouble()
                    } catch (e: NotANumberException) {
                        continue
                    }
                    // el is a number
                    right -= (left as Sum)[el]
                    (left as Sum).removeAt(el)
                    left = left.simplify()
                    return separateVariables()
                }
            }
            is Product -> {
                for (el in (left as Product).indices.reversed()) {
                    try {
                        (left as Product)[el].toDouble()
                    } catch (e: NotANumberException) {
                        continue
                    }
                    // el is a number
                    right /= (left as Product)[el]
                    (left as Product).removeAt(el)
                    left = left.simplify()
                    return separateVariables()
                }
            }
            is Num -> {
                if (left == right.simplify()) return listOf(mutableMapOf())
                else throw NoCutException()
            }
            is Variable -> return listOf(mutableMapOf(Pair(left as Variable, right.simplify())))
            else -> throw NotImplementedError("Operation is not implemented for class ${left.javaClass}")
        }
        println(this)
        throw NotImplementedError("Can't solve this equation")
    }

    class NoCutException : Throwable()

    override fun toString(): String {
        return "$left = $right"
    }
}
