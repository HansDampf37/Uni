package org.deg.uni.algebra.functions.norms

import org.deg.uni.algebra.Matrix
import org.deg.uni.analysis.Field
import org.deg.uni.analysis.sum

class Frobeniusnorm2<T: Field<T>>: Norm<Matrix<T>, T> {
    override fun l(element: Matrix<T>): T {
        return element.elements().map { it * it }.sum()
    }
}