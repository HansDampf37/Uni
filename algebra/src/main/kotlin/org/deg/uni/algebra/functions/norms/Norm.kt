package org.deg.uni.algebra.functions.norms

interface Norm<T, S> {
    fun l(element: T): S
}