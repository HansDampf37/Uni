import analysis.terms.Term
import analysis.terms.parsing.LexiAnalysis
import analysis.terms.parsing.SyntacticAnalysis

fun String.toTerm(): Term {
    val tokensAndAssignment = LexiAnalysis().parse(this)
    return SyntacticAnalysis().parse(tokensAndAssignment.first, tokensAndAssignment.second)
}