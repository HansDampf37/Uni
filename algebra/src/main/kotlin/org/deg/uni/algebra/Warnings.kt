package org.deg.uni.algebra

object Warnings {
    private const val throwWarningsHigherThan = -1

    fun warn(str: String, degree: Int) {
        if (degree > throwWarningsHigherThan) throw Exception(str)
        else Exception(str).printStackTrace()
    }
}