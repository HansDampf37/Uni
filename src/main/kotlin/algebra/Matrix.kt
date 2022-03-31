package algebra

import analysis.Field

/**
 * Matrix is a List of transposed Vectors
 *
 * @param T Type of contained elements
 *
 * @param entries 2d-List of contained elements
 */
open class Matrix<T : Field<T>>(entries: List<List<T>>) : Cloneable, ArrayList<Vec<T>>(), Field<Matrix<T>> {

    constructor(vararg rows: Vec<T>) : this(rows.toList())

    constructor(
        width: Int,
        height: Int,
        init: (i: Int, j: Int) -> T
    ) : this(List<List<T>>(height) { i -> List(width) { j -> init(i, j) } })

    init {
        if (entries.isEmpty()) throw IllegalArgumentException("Can't create empty matrix.")
        if (entries.any { it.size != entries.first().size }) throw IllegalArgumentException("Given array is not a Matrix")
        for (row in entries) {
            this.add(Vec(row))
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
    val cols: Iterable<Vec<T>> get() = List(this[0].size) { i -> Vec(map { it[i] }) }

    /**
     * the [1][Field.one] of the contained elements
     */
    val oneElement = entries[0][0].one()

    /**
     * the [0][Field.zero] of the contained elements
     */
    val zeroElement = entries[0][0].zero()

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
    val determinant: T
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
    override fun times(other: Matrix<T>): Matrix<T> {
        if (other.height != width) Warnings.warn("Unequal Dimensions", 0)
        val newEntries: Matrix<T> = Matrix(other.width, height) { i, j -> row(i) * other.col(j) }
        return Matrix(newEntries)
    }

    /**
     * Div multiplies this with the [inverse][inverseMult] of [other].
     *
     * @param other Matrix
     * @return this * other⁻¹
     */
    override fun div(other: Matrix<T>): Matrix<T> = times(inverseMult(other))

    /**
     * Plus adds two matrices elementwise
     *
     * @param other Matrix
     * @return this + other
     */
    override fun plus(other: Matrix<T>): Matrix<T> {
        return Matrix(zipWith(other) { rows1, rows2 -> rows1.zipWith(rows2) { c1, c2 -> c1 + c2 } })
    }

    /**
     * Minus subtracts two matrices elementwise
     *
     * @param other Matrix
     * @return this - other
     */
    override fun minus(other: Matrix<T>): Matrix<T> {
        if (other.height != height || other.width != width) throw IllegalArgumentException("Unequal height or width: \n$this and \n$other")
        return Matrix(zipWith(other) { rows1, rows2 -> rows1.zipWith(rows2) { c1, c2 -> c1 - c2 } })
    }

    /**
     * Zero returns a matrix with the same dimensions as this one but filled with [0][Field.zero]
     *
     * @return zero-matrix
     */
    override fun zero(): Matrix<T> {
        return Matrix(width, height) { i, j -> this[i][j].zero() }
    }

    /**
     * One returns a matrix with the same dimensions as this one but filled with [0][Field.zero] except when i == j -> this(i, j) = [1][Field.one]
     *
     * @return identity-matrix
     */
    override fun one(): Matrix<T> {
        return Matrix(List(height) { i -> List(width) { j -> if (i == j) oneElement else zeroElement }.toList() })
    }

    /**
     * Inverse mult returns a matrix m so that e * m = [I_n][one]
     *
     * @param e Matrix
     * @return e⁻¹
     * @throws NotRegularException if this matrix doesn't have an inverse
     */
    override fun inverseMult(e: Matrix<T>): Matrix<T> {
        val extended = extendCols(one().subMatrix(0, minOf(height, width), 0, maxOf(height, width)))
        val (res, det) = extended.toStaircase()
        if (det == zeroElement) throw NotRegularException()
        for (j in 1 until res.height) {
            for (upperRow in 0 until j) {
                if (res[upperRow][j] == res.zeroElement) continue
                res.addRowToRow(j, res.zeroElement - res[upperRow][j], upperRow)
            }
        }
        return res.subMatrix(0, res.height, width, res.width)
    }

    /**
     * Inverse add returns a matrix m so that e + m = [0][zero]
     *
     * @return -e
     */
    override fun inverseAdd(e: Matrix<T>): Matrix<T> {
        return Matrix(map { row -> row.map { it.zero() - it } })
    }

    /**
     * Times multiplies a vector with this matrix
     *
     * @param v Vector
     * @return this * v
     */
    operator fun times(v: Vec<T>): Vec<T> {
        if (v.size != width) Warnings.warn("Unequal Dimensions", 0)
        val newEntries: ArrayList<T> = v.zero()
        for (i in rowIndices) {
            newEntries[i] = row(i) * v
        }
        return Vec(newEntries)
    }

    /**
     * Extend cols returns the matrix this | m
     *
     * @param m Matrix with same height as this
     * @return this | m
     */
    fun extendCols(m: Matrix<T>): Matrix<T> {
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
    fun extendRows(m: Matrix<T>): Matrix<T> {
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
    fun subMatrix(fromRow: Int, toRow: Int, fromCol: Int, toCol: Int): Matrix<T> {
        if (fromRow >= height || fromRow < 0 || fromCol >= width || fromCol < 0) throw IndexOutOfBoundsException()
        if (fromCol >= toCol || fromRow >= toRow) throw IllegalArgumentException("endpoint indices are out of order (fromIndex >= toIndex)")
        return Matrix(toCol - fromCol, toRow - fromRow) { i, j ->
            this[i + fromRow][j + fromCol]
        }
    }

    /**
     * Transpose returns the transposed matrix (this(i,j) == transposed(j, i))
     *
     * @return
     */
    fun transpose(): Matrix<T> {
        return Matrix(height, width) { i, j -> this[j][i] }
    }

    /**
     * To staircase applies the gauss algorithm on a copy of this matrix
     *
     * @return StaircaseMatrix, Determinant
     */
    fun toStaircase(): Pair<Matrix<T>, T> {
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
                if (under != zeroElement) res.addRowToRow(i, under.zero() - under, i2)
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
    fun addRowToRow(addThisRow: Int, scaledWith: T, ontoThisRow: Int) {
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

    fun col(j: Int) = Vec(map { it[j] })

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

    override fun clone(): Matrix<T> {
        val res = ArrayList<ArrayList<T>>(height)
        for (i in rowIndices) {
            res.add(ArrayList(width))
            for (j in columnIndices) {
                res[i].add(this[i][j])
            }
        }
        return Matrix(res)
    }

    /**
     * Elements returns all elements in reading order.
     *
     * @return
     */
    fun elements(): List<T> {
        return this.flatten()
    }
}

class NotRegularException : Exception()

