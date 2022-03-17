package algebra.structures

import algebra.Vec
import analysis.Field
import analysis.GroupAdd

interface VectorSpace<T: Field<T>>: GroupAdd<Vec<T>> {

    operator fun times(scalar: T) : Vec<T>
    operator fun div(scalar: T) = times(scalar.inverseMult(scalar))
}
