package analysis.terms

class Const(private val str: String, double: Double): Num(double) {
    override fun toString() = str
}