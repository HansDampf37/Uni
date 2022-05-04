package org.deg.uni.algebra

import org.deg.uni.analysis.Field
import org.deg.uni.analysis.sum
import org.deg.uni.analysis.terms.model.Num
import org.deg.uni.analysis.terms.model.Term
import org.deg.uni.analysis.unaryMinus
import org.deg.uni.utils.zipWith
import kotlin.math.sqrt

class SimpleVec(entries: List<Double>) : ArrayList<Double>() {

    constructor(vararg comps: Double) : this(comps.toList())

    @Suppress("UNCHECKED_CAST")
    constructor(vararg comps: Number): this(comps.map {it.toDouble()})

    constructor(size: Int, operation: (Int) -> Double): this(List(size, operation))

    init {
        for (e in entries) add(e)
    }

    var x
        get() = this[0]
        set(value) {
            this[0] = value
        }
    var y
        get() = this[1]
        set(value) {
            this[1] = value
        }
    var z
        get() = this[2]
        set(value) {
            this[2] = value
        }
    var w
        get() = this[3]
        set(value) {
            this[3] = value
        }

    /**
     * Plus adds two Vectors of equal sizes
     *
     * @param other Vector
     * @return this + other
     */
    operator fun plus(other: SimpleVec): SimpleVec {
        if (other.size != size) Warnings.warn("Unequal Dimensions", 0)
        return SimpleVec(this.zipWith(other) { one: Double, two: Double -> one + two })
    }

    /**
     * Minus subtracts two Vectors of equal sizes
     *
     * @param other Vector
     * @return this - other
     */
    operator fun minus(other: SimpleVec): SimpleVec {
        if (other.size != size) Warnings.warn("Unequal Dimensions", 0)
        return SimpleVec(this.zipWith(other) { x: Double, y: Double -> x - y })
    }

    /**
     * Times multiplies this Vector with a scalar
     *
     * @param scalar the scalar
     * @return scalar * this
     */
    operator fun times(scalar: Double) : SimpleVec {
        return SimpleVec(this.map { it * scalar })
    }

    /**
     * Div divides this Vector with a scalar
     *
     * @param scalar the scalar
     * @return scalar / this
     */
    operator fun div(scalar: Double) : SimpleVec {
        return SimpleVec(this.map { it / scalar })
    }

    /**
     * Times scalar-multiplies this Vector with another vector
     *
     * @param other Vector
     * @return this * other
     */
    operator fun times(other: SimpleVec): Double {
        if (other.size != size) Warnings.warn("Unequal Dimensions", 0)
        return this.zipWith(other) { x: Double, y: Double -> x * y }.sum()
    }

    /**
     * Zero returns a Vector with equal size and filled with [zeros][Field.zero]
     *
     * @return a Vector with equal size and filled with [zeros][Field.zero]
     */
    fun zero(): SimpleVec {
        return SimpleVec(List(size) { 0.0 })
    }

    /**
     * Inverse add returns a vector v that satisfies v + e = [0][zero]
     *
     * @return additive inverse of e
     */
    fun inverseAdd(): SimpleVec {
        return SimpleVec(this.map { -it })
    }

    /**
     * Cut returns a vector that contains the elements from start (incl) to endExcl (excl) of this vector.
     *
     * @param start start index
     * @param endExcl end index
     * @return a vector that contains the elements from start (incl) to endExcl (excl) of this vector.
     * @throws IndexOutOfBoundsException – for an illegal endpoint index value (fromIndex < 0 || toIndex > size || fromIndex > toIndex)
     * @throws IllegalArgumentException – if the endpoint indices are out of order (fromIndex > toIndex)
     */
    fun cut(start: Int = 0, endExcl: Int): SimpleVec {
        return SimpleVec(this.subList(start, endExcl))
    }

    /**
     * Extend extends a vector to a given size.
     *
     * @param targetSize size of the resulting vector
     * @param operation initialization function
     * @return new bigger Vector
     * @throws IllegalArgumentException if [targetSize] < [size]
     */
    fun extend(targetSize: Int, operation: (Int) -> Double): SimpleVec {
        val vec = clone()
        vec.addAll(List(targetSize - size) { i -> operation(i + size) })
        return vec
    }

    /**
     * Equals returns true if and only if the [other] is a vector which has the same [size] as this vector and contains
     * the same elements
     *
     * @param other Vector
     * @return true or false as described
     */
    override fun equals(other: Any?): Boolean {
        if (other !is SimpleVec) return false
        if (other.size != this.size) return false
        for (i in indices) if (this[i] != other[i]) return false
        return true
    }

    override fun toString(): String {
        return this.joinToString(",", "[", "]") { it.toString() }
    }

    override fun clone(): SimpleVec {
        return SimpleVec(this.toList())
    }

    /**
     * To matrix returns a vector into a size x 1 matrix
     *
     * @return
     */
    fun toMatrix(): SimpleMatrix {
        return SimpleMatrix(this.map { SimpleVec(it) })
    }

}

/**
 * Len returns the vectors length
 *
 * @return the vectors length
 */
fun SimpleVec.len(): Double {
    return sqrt(this.map { it * it }.sum())
}
