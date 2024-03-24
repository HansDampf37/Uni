package org.deg.uni.api.dtos

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.deg.uni.analysis.terms.model.*


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = SumDTO::class, name = "sum"),
    JsonSubTypes.Type(value = NumDTO::class, name = "num"),
    JsonSubTypes.Type(value = ProductDTO::class, name = "product"),
    JsonSubTypes.Type(value = VariableDTO::class, name = "variable"),
    JsonSubTypes.Type(value = PowerDTO::class, name = "power")
)
interface TermDTO {
    fun toTerm(): Term

    companion object {
        fun fromTerm(term: Term): TermDTO {
            return when(term) {
                is Sum -> SumDTO(term.map { fromTerm(it) })
                is Product -> ProductDTO(term.map{ fromTerm(it) })
                is Variable -> VariableDTO(term.str)
                is Num -> NumDTO(term.num)
                is Power -> PowerDTO(fromTerm(term.base), fromTerm(term.exponent))
                else -> throw NotImplementedError(term::class.java.simpleName)
            }
        }
    }
}