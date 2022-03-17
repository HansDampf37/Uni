package algebra.functions.norms

import algebra.Matrix
import analysis.Field
import analysis.sum

class Frobeniusnorm2<T: Field<T>>: Norm<Matrix<T>, T> {
    override fun l(element: Matrix<T>): T {
        return element.elements().map { it * it }.sum()
    }
}