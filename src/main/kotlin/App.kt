import algebra.Matrix
import algebra.Vec
import analysis.terms.Term

fun main() {
    val m = Matrix<Term>(Vec(1, 2, 0), Vec(-3, 1, 0), Vec(0, 0, 1))
    val v = Vec<Term>(4, -2, 2)
    println("$m * $v = ${m * v}")
    println("${m.toStaircase().first}")
    println("det(\n$m) = ${m.determinant}")
}