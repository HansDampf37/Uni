package analysis.terms

class Sum(terms: List<Term>) : ArrayList<Term>(), Term {

    constructor(vararg terms: Term) : this(terms.toList())

    init {
        for (t in terms) {
            /*if (t is Sum) {
                addAll(t)
            } else {
                add(t)
            }*/
            add(t)
        }
    }

    override fun plus(other: Term): Term = other + this
    override fun times(other: Term): Term = other * this

    override fun contains(x: Variable): Boolean = any { it.contains(x) }
    override fun derive(x: Variable): Term = Sum(map{ it.derive(x) }).simplify()

    override fun simplifier(): Simplifier<Sum> = SumSimplifier()

    override fun toInt(): Int = sumOf { it.toInt() }
    override fun toDouble(): Double = sumOf { it.toDouble() }
    override fun toString() = joinToString(" + ") { t: Term -> t.toString() }

    override fun clone(): Sum = Sum(this.toList())
    override fun equals(other: Any?): Boolean {
        if (other is Sum) {
            return this.all { other.contains(it) } && other.all { this.contains(it) }
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}