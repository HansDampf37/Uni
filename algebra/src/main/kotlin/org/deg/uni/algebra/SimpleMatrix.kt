package org.deg.uni.algebra

import org.deg.uni.analysis.Field
import org.deg.uni.analysis.unaryMinus
import org.deg.uni.utils.addPadding
import org.deg.uni.utils.zipWith

/**
 * Matrix is a List of transposed Vectors
 *
 * @param entries 2d-List of contained elements
 */
open class SimpleMatrix(entries: List<List<Double>>) : Cloneable, ArrayList<SimpleVec>(), Field<SimpleMatrix> {

    constructor(vararg rows: SimpleVec) : this(rows.toList())

    constructor(
        width: Int,
        height: Int,
        init: (i: Int, j: Int) -> Double
    ) : this(List<List<Double>>(height) { i -> List(width) { j -> init(i, j) } })

    init {
        if (entries.isEmpty()) throw IllegalArgumentException("Can't create empty matrix.")
        if (entries.any { it.size != entries.first().size }) throw IllegalArgumentException("Given array is not a Matrix")
        for (row in entries) {
            this.add(SimpleVec(row))
        }
    }

    /**
     * The matrices height
     */
    val height get() = size

    /**
     * The matrices with
     */
    val width get() = this[0].size
    private val rowIndices: IntRange get() = 0 until height
    private val columnIndices: IntRange get() = 0 until width

    /**
     * List of column vectors
     */
    val cols: Iterable<SimpleVec> get() = List(this[0].size) { i -> SimpleVec(map { it[i] }) }

    /**
     * the [1][Field.one] of the contained elements
     */
    val oneElement = 1.0

    /**
     * the [0][Field.zero] of the contained elements
     */
    val zeroElement = 0.0

    /**
     * True if this(i,j) == this(j, i) for all i,j in 0 until height, 0 until width
     */
    val symmetrical: Boolean
        get() {
            if (height != width) return false
            for (i in rowIndices) {
                for (j in columnIndices) {
                    if (this[j][i] != this[i][j]) return false
                }
            }
            return true
        }
    val determinant: Double
        get() {
            return if (height == width) {
                toStaircase().second
            } else {
                zeroElement
            }
        }

    /**
     * Returns true if and only if determinant != 0
     */
    val regular: Boolean get() = determinant != zeroElement

    /**
     * Returns true if and only if the vectors in this matrix are orthogonal to each other
     */
    val orthogonal: Boolean get() = this * transpose() == this.one()

    /**
     * Times multiplies two matrices
     *
     * @param other Matrix
     * @return this * other
     */
    override fun times(other: SimpleMatrix): SimpleMatrix {
        if (other.height != width) Warnings.warn("Unequal Dimensions", 0)
        val newEntries: SimpleMatrix = SimpleMatrix(other.width, height) { i, j -> row(i) * other.col(j) }
        return SimpleMatrix(newEntries)
    }

    /**
     * Div multiplies this with the [inverse][inverseMult] of [other].
     *
     * @param other Matrix
     * @return this * other⁻¹
     */
    override fun div(other: SimpleMatrix): SimpleMatrix = times(other.inverseMult())

    /**
     * Plus adds two matrices elementwise
     *
     * @param other Matrix
     * @return this + other
     */
    override fun plus(other: SimpleMatrix): SimpleMatrix {
        return SimpleMatrix(zipWith(other) { rows1, rows2 -> rows1.zipWith(rows2) { c1, c2 -> c1 + c2 } })
    }

    /**
     * Minus subtracts two matrices elementwise
     *
     * @param other Matrix
     * @return this - other
     */
    override fun minus(other: SimpleMatrix): SimpleMatrix {
        if (other.height != height || other.width != width) throw IllegalArgumentException("Unequal height or width: \n$this and \n$other")
        return SimpleMatrix(zipWith(other) { rows1, rows2 -> rows1.zipWith(rows2) { c1, c2 -> c1 - c2 } })
    }

    /**
     * Zero returns a matrix with the same dimensions as this one but filled with [0][Field.zero]
     *
     * @return zero-matrix
     */
    override fun zero(): SimpleMatrix {
        return SimpleMatrix(width, height) { _, _ -> 0.0 }
    }

    /**
     * One returns a matrix with the same dimensions as this one but filled with [0][Field.zero] except when i == j -> this(i, j) = [1][Field.one]
     *
     * @return identity-matrix
     */
    override fun one(): SimpleMatrix {
        return SimpleMatrix(List(height) { i -> List(width) { j -> if (i == j) oneElement else zeroElement }.toList() })
    }

    /**
     * Inverse mult returns a matrix m so that m * m⁻¹ = [I_n][one]
    1    *
     * @return this⁻¹
     * @throws NotRegularException if this matrix doesn't have an inverse
     */
    override fun inverseMult(): SimpleMatrix {
        val extended = extendCols(one().subMatrix(0, minOf(height, width), 0, maxOf(height, width)))
        val (res, det) = extended.toStaircase()
        if (det == zeroElement) throw NotRegularException()
        for (j in 1 until res.height) {
            for (upperRow in 0 until j) {
                if (res[upperRow][j] == res.zeroElement) continue
                res.addRowToRow(j, -res[upperRow][j], upperRow)
            }
        }
        return res.subMatrix(0, res.height, width, res.width)
    }

    /**
     * Inverse add returns a matrix m so that e + m = [0][zero]
     *
     * @return -e
     */
    override fun inverseAdd(): SimpleMatrix {
        return SimpleMatrix(map { row -> row.map { -it } })
    }

    /**
     * Times multiplies a vector with this matrix
     *
     * @param v Vector
     * @return this * v
     */
    operator fun times(v: SimpleVec): SimpleVec {
        if (v.size != width) Warnings.warn("Unequal Dimensions", 0)
        val newEntries: ArrayList<Double> = v.zero()
        for (i in rowIndices) {
            newEntries[i] = row(i) * v
        }
        return SimpleVec(newEntries)
    }

    /**
     * Extend cols returns the matrix this | m
     *
     * @param m Matrix with same height as this
     * @return this | m
     */
    fun extendCols(m: SimpleMatrix): SimpleMatrix {
        if (m.height != height) throw IllegalArgumentException("Unequal height: \n$this and \n$m")
        val res = clone()
        for (i in 0 until m.height) {
            for (el in m[i]) {
                res[i].add(el)
            }
        }
        return res
    }

    /**
     * Extend rows returns the matrix
     *
     * this
     *
     * _
     *
     * m
     *
     * @param m Matrix with the same width as this
     * @return new matrix as described
     */
    fun extendRows(m: SimpleMatrix): SimpleMatrix {
        if (m.width != width) throw IllegalArgumentException("Unequal width")
        val res = clone()
        res.addAll(m)
        return res
    }

    /**
     * Submatrix returns a matrix containing the specified elements
     *
     * @param fromRow lower bound for row index (inklusive)
     * @param toRow upper bound for row index (exclusive)
     * @param fromCol lower bound for column index (inklusive)
     * @param toCol upper bound for column index (exclusive)
     * @return submatrix as explained
     * @throws IndexOutOfBoundsException – for an illegal endpoint index value (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
     * @throws IllegalArgumentException – if the endpoint indices are out of order (fromIndex >= toIndex)
     */
    fun subMatrix(fromRow: Int, toRow: Int, fromCol: Int, toCol: Int): SimpleMatrix {
        if (fromRow >= height || fromRow < 0 || fromCol >= width || fromCol < 0) throw IndexOutOfBoundsException()
        if (fromCol >= toCol || fromRow >= toRow) throw IllegalArgumentException("endpoint indices are out of order (fromIndex >= toIndex)")
        return SimpleMatrix(toCol - fromCol, toRow - fromRow) { i, j ->
            this[i + fromRow][j + fromCol]
        }
    }

    /**
     * Transpose returns the transposed matrix (this(i,j) == transposed(j, i))
     *
     * @return
     */
    fun transpose(): SimpleMatrix {
        return SimpleMatrix(height, width) { i, j -> this[j][i] }
    }

    /**
     * To staircase applies the gauss algorithm on a copy of this matrix
     *
     * @return StaircaseMatrix, Determinant
     */
    fun toStaircase(): Pair<SimpleMatrix, Double> {
        val res = clone()
        var determinant = oneElement
        for (i in 0 until minOf(height, width)) {
            var pivot = res[i][i]
            if (pivot == zeroElement) {
                val index = res.subList(i, res.height).indexOfFirst { it[i] != zeroElement } + i
                if (index == -1) continue
                res.swapLines(index, i)
                determinant *= zeroElement - oneElement
                pivot = res[i][i]
            }
            res[i] = res[i] / pivot
            determinant *= pivot
            for (i2 in i + 1 until minOf(height, width)) {
                val under = res[i2][i]
                if (under != zeroElement) res.addRowToRow(i, -under, i2)
            }
        }
        return Pair(res, determinant)
    }

    /**
     * Add row to row
     *
     * @param addThisRow rowIndex
     * @param scaledWith scalar
     * @param ontoThisRow rowIndex
     */
    fun addRowToRow(addThisRow: Int, scaledWith: Double, ontoThisRow: Int) {
        this[ontoThisRow] = this[ontoThisRow] + this[addThisRow] * scaledWith
    }

    /**
     * Swaps rows
     *
     * @param i1 index
     * @param i2 index
     */
    fun swapLines(i1: Int, i2: Int) {
        val temp = this[i1]
        this[i1] = this[i2]
        this[i2] = temp
    }

    fun row(i: Int) = this[i]

    fun col(j: Int) = SimpleVec(map { it[j] })

    override fun toString(): String {
        val widthPerCol = Array(width) { col(it).maxOf { el -> el.toString().length } }
        // val maxWidth = elements().maxOf { el -> el.toString().length }
        val strBuilder = StringBuilder()
        for (i in rowIndices) {
            strBuilder.append("|")
            for (j in columnIndices) {
                strBuilder.append(addPadding(this[i][j].toString(), widthPerCol[j], ' '))
                if (j != width - 1) strBuilder.append(",  ")
            }
            strBuilder.append("|")
            if (i != height - 1) strBuilder.append("\n")
        }
        return strBuilder.toString()
    }

    override fun clone(): SimpleMatrix {
        val res = ArrayList<ArrayList<Double>>(height)
        for (i in rowIndices) {
            res.add(ArrayList(width))
            for (j in columnIndices) {
                res[i].add(this[i][j])
            }
        }
        return SimpleMatrix(res)
    }

    /**
     * Elements returns all elements in reading order.
     *
     * @return
     */
    fun elements(): List<Double> {
        return this.flatten()
    }
}