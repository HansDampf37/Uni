package org.deg.uni.algebra.typewrapper

import org.deg.uni.analysis.Field

class StringWrapper(private val str: String) : Field<StringWrapper> {
    override fun plus(other: StringWrapper): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun inverseAdd(): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun zero(): StringWrapper = StringWrapper("")

    override fun times(other: StringWrapper): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun inverseMult(): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun minus(other: StringWrapper): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun div(other: StringWrapper): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun one(): StringWrapper = StringWrapper("")

    override fun toString(): String {
        return str
    }
}
