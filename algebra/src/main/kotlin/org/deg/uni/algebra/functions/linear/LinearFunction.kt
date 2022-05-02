package org.deg.uni.algebra.functions.linear

import org.deg.uni.algebra.Matrix
import org.deg.uni.algebra.Vec
import org.deg.uni.analysis.Field

class LinearFunction<T: Field<T>>(private val m: Matrix<T>): F<Vec<T>, Vec<T>> {
    override fun f(x: Vec<T>): Vec<T> = m * x

    fun concatenate(other: LinearFunction<T>): LinearFunction<T> {
        return LinearFunction(m * other.m)
    }
}