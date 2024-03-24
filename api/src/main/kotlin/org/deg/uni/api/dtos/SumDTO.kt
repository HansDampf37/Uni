package org.deg.uni.api.dtos

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.deg.uni.analysis.terms.model.Sum

@JsonTypeName("sum")
data class SumDTO @JsonCreator constructor(@JsonProperty("summands") val summands: List<TermDTO>): TermDTO {
    override fun toTerm(): Sum {
        return Sum(summands.map { it.toTerm() })
    }
}