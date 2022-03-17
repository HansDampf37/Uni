package algebra.typewrapper

import analysis.Field

class StringWrapper(private val str: String) : Field<StringWrapper> {
    override fun plus(other: StringWrapper): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun inverseAdd(e: StringWrapper): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun zero(): StringWrapper = StringWrapper("")

    override fun times(other: StringWrapper): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun inverseMult(e: StringWrapper): StringWrapper {
        TODO("Not yet implemented")
    }

    override fun one(): StringWrapper = StringWrapper("")

    override fun toString(): String {
        return str
    }
}
