package org.deg.uni.analysis

interface Field<T> : GroupAdd<T>, GroupMult<T>

fun <T> T.sign(): T where T : Comparable<T>, T : Field<T> {
    return if (this >= this.zero()) this.one() else -this.one()
}

