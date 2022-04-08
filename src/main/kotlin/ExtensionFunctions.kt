import analysis.terms.model.Num
import analysis.terms.model.Term
import analysis.terms.parsing.LexiAnalysis
import analysis.terms.parsing.SyntacticAnalysis

fun String.toTerm(): Term {
    val tokensAndAssignment = LexiAnalysis().parse(this)
    return SyntacticAnalysis().parse(tokensAndAssignment.first, tokensAndAssignment.second)
}

fun String.toNum(): Num {
    if (contains("/")) {
        val parts = split("/")
        val num = parts[0].toDouble()
        val denom = parts[1].toDouble()
        return Num(num, denom)
    }
    return Num(toDouble())
}