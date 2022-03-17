package algebra.functions.norms

interface Norm<T, S> {
    fun l(element: T): S
}