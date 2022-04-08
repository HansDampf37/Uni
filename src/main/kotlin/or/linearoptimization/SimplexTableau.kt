package or.linearoptimization

import algebra.Matrix
import algebra.Vec
import algebra.typewrapper.StringWrapper
import analysis.terms.model.Num
import analysis.terms.model.Term

class SimplexTableau(
    opt: OptimizationProblem.Optimize,
    constraints: MutableList<OptimizationProblem.Constraint>
) {
    var m: Matrix<Term>
    val baseVariables: MutableList<String> =
        MutableList(constraints.count { it.relation != OptimizationProblem.Relation.EQ }) { i -> "s${i + 1}" }
    val nonBaseVariables: MutableList<String> = MutableList(opt.parameters.size) { j -> "x${j + 1}" }
    private val variables: MutableList<String>
        get() {
            val res = MutableList(nonBaseVariables.size) { i -> "x${i + 1}" }
            res.addAll(MutableList(baseVariables.size) { i -> "s${i + 1}" })
            return res
        }
    val z: Double get() = m[0][m.width - 1].toDouble()
    val currentSolution: List<Pair<String, Term>>
        get() = List(m.height - 1) { i ->
            Pair(
                baseVariables[i],
                m[i + 1][m.width - 1]
            )
        }

    init {
        opt.toMax()
        var amountOfS = 0
        for (c in constraints) {
            if (c.relation != OptimizationProblem.Relation.EQ) {
                opt.parameters.add(Num(0))
                if (c.relation == OptimizationProblem.Relation.GET) {
                    c.relation = OptimizationProblem.Relation.LET
                    c.parameters *= Num(-1)
                    c.rightSide *= -1
                }
                amountOfS++
                for (c1 in constraints) {
                    if (c1 == c) c1.parameters.add(Num(1))
                    else c1.parameters.add(Num(0))
                }
            }
        }
        val rows: MutableList<Vec<Term>> =
            listOf(Vec(opt.parameters * Num(-1)).apply { add(Num(0)) }).toMutableList()
        val cs: List<Vec<Term>> =
            constraints.map { c -> Vec(c.parameters).apply { add(Num(c.rightSide)) } }
        rows.addAll(cs)
        m = Matrix(rows)
    }

    override fun toString(): String {
        val headLine = MutableList(nonBaseVariables.size) { i -> StringWrapper("x${i + 1}") }
        headLine.addAll(MutableList(baseVariables.size) { i -> StringWrapper("s${i + 1}") })
        headLine.add(StringWrapper(""))
        var res = Matrix(Vec(headLine))
        val mStr: Matrix<StringWrapper> = Matrix(m.width, m.height) { i, j -> StringWrapper(m[i][j].toString()) }
        res = res.extendRows(mStr)
        val left = MutableList(m.height + 1) { i ->
            when (i) {
                0 -> StringWrapper("")
                1 -> StringWrapper("z")
                else -> StringWrapper("0")
            }
        }
        return Matrix(left.map { listOf(it) }).extendCols(res).toString()
    }

    fun shortString(): String {
        val mStr: Matrix<StringWrapper> = Matrix(m.width, m.height) { i, j -> StringWrapper(m[i][j].toString()) }
        for (j in variables.indices.reversed()) {
            if (baseVariables.contains(variables[j])) {
                mStr.forEach { it.removeAt(j) }
            }
        }
        var res = Matrix(Vec(nonBaseVariables.map { StringWrapper(it) }.toMutableList().apply{ add(StringWrapper("")) }))
        res = res.extendRows(mStr)
        val left = MutableList(m.height + 1) { i ->
            when (i) {
                0 -> StringWrapper("")
                1 -> StringWrapper("z")
                else -> StringWrapper(baseVariables[i - 2])
            }
        }
        return Matrix(left.map { listOf(it) }).extendCols(res).toString()
    }
}