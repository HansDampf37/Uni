package analysis.terms

class Log(var base: Term, var arg: Term): Term {
    // TODO add plus(log: Log) to term
    // TODO add times(log: Log) to term
    override fun plus(other: Term): Term = other + this
    override fun times(other: Term): Term  = other * this

    override fun clone() = Log(base, arg)

}