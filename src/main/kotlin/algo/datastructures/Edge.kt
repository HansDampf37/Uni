package algo.datastructures

class Edge<T, S>(val from: INode<T>, val to: INode<T>, val weight: Double = 0.0, val el: S? = null) {
    override fun toString(): String {
        return "$from - $to" + if (el == null) "" else " ($el)"
    }
}