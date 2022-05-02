package org.deg.uni.analysis.terms.parsing

enum class Token(val regex: Regex) {
    NEGATIVE(Regex("-")),
    POSITIVE(Regex("\\+")),
    STAR(Regex("\\*")),
    SLASH(Regex("/")),
    PLUS(Regex("\\+")),
    MINUS(Regex("-")),
    POWER(Regex("\\^")),
    SIN(sinRegex),
    COS(cosRegex),
    TAN(tanRegex),
    LOG(logRegex),
    LOG_10(log10Regex),
    LOG_2(log2Regex),
    LN(LnRegex),
    OPEN_BRACKET(Regex("\\(")),
    CLOSE_BRACKET(Regex("\\)")),
    VAR(Regex("^(?!.*$functionRegex)[a-zA-Z][0-9]*")),
    NUM(Regex("([1-9][0-9]*(\\.[0-9]+)?)|[1-9][0-9]*/[1-9][0-9]*")),
    EOF(Regex(""))
}

private val functionRegex = Regex("log|Log|LOG|sin|Sin|SIN|cos|Cos|COS|tan|Tan|TAN")
private val sinRegex = Regex("sin|Sin|SIN")
private val cosRegex = Regex("cos|Cos|COS")
private val tanRegex = Regex("tan|Tan|TAN")
private val logRegex = Regex("log|Log|LOG")
private val log10Regex = Regex("log10|Log10|LOG10")
private val log2Regex = Regex("log2|Log2|LOG2")
private val LnRegex = Regex("logE|LogE|LOGE|LN|ln|Ln")