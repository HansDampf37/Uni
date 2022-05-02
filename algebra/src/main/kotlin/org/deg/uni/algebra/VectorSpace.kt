package org.deg.uni.algebra

import org.deg.uni.analysis.Field
import org.deg.uni.analysis.GroupAdd

interface VectorSpace<T: Field<T>>: GroupAdd<Vec<T>> {

    operator fun times(scalar: T) : Vec<T>
    operator fun div(scalar: T) = times(scalar.inverseMult())
}
