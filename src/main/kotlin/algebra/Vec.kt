package algebra

import algebra.structures.VectorSpace
import analysis.Field
import analysis.sum
import analysis.terms.model.Num
import analysis.terms.model.Power
import analysis.terms.model.Term
import analysis.unaryMinus

class Vec<T: Field<T>>(entries: List<T>) : ArrayList<T>(), VectorSpace<T> {

    constructor(vararg comps: T) : this(comps.toList())

    @Suppress("UNCHECKED_CAST")
    constructor(vararg comps: Number): this(comps.map { Num(it) } as List<T>)

    constructor(size: Int, operation: (Int) -> T): this(List(size, operation))

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
    override operator fun plus(other: Vec<T>): Vec<T> {
        if (other.size != size) Warnings.warn("Unequal Dimensions", 0)
        return Vec(this.zipWith(other) { one: T, two: T -> one + two })
    }

    /**
     * Minus subtracts two Vectors of equal sizes
     *
     * @param other Vector
     * @return this - other
     */
    override operator fun minus(other: Vec<T>): Vec<T> {
        if (other.size != size) Warnings.warn("Unequal Dimensions", 0)
        return Vec(this.zipWith(other) { x: T, y: T -> x - y })
    }

    /**
     * Times multiplies this Vector with a scalar
     *
     * @param scalar the scalar
     * @return scalar * this
     */
    override operator fun times(scalar: T) : Vec<T> {
        return Vec(this.map { it * scalar })
    }

    /**
     * Times scalar-multiplies this Vector with another vector
     *
     * @param other Vector
     * @return this * other
     */
    operator fun times(other: Vec<T>): T {
        if (other.size != size) Warnings.warn("Unequal Dimensions", 0)
        return this.zipWith(other) { x: T, y: T -> x * y }.sum()
    }

    /**
     * Zero returns a Vector with equal size and filled with [zeros][Field.zero]
     *
     * @return a Vector with equal size and filled with [zeros][Field.zero]
     */
    override fun zero(): Vec<T> {
        return Vec(List(size) { this[it].zero() })
    }

    /**
     * Inverse add returns a vector v that satisfies v + e = [0][zero]
     *
     * @return additive inverse of e
     */
    override fun inverseAdd(): Vec<T> {
        return Vec(this.map { -it })
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
    fun cut(start: Int = 0, endExcl: Int): Vec<T> {
        return Vec(this.subList(start, endExcl))
    }

    /**
     * Extend extends a vector to a given size.
     *
     * @param targetSize size of the resulting vector
     * @param operation initialization function
     * @return new bigger Vector
     * @throws IllegalArgumentException if [targetSize] < [size]
     */
    fun extend(targetSize: Int, operation: (Int) -> T): Vec<T> {
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
        if (other !is Vec<*>) return false
        if (other.size != this.size) return false
        for (i in indices) if (this[i] != other[i]) return false
        return true
    }

    override fun toString(): String {
        return this.joinToString(",", "[", "]") { it.toString() }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun clone(): Vec<T> {
        return Vec(this.toList())
    }

    /**
     * To matrix returns a vector into a size x 1 matrix
     *
     * @return
     */
    fun toMatrix(): Matrix<T> {
        return Matrix(this.map { Vec(it) })
    }

}

/**
 * Len returns the vectors length
 *
 * @return the vectors length
 */
fun Vec<Term>.len(): Term {
    return this.map { it * it }.sum().sqrt()
}
