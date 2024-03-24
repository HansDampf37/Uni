package org.deg.uni.api.dtos

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.deg.uni.analysis.terms.model.Num

@JsonTypeName("num")
data class NumDTO @JsonCreator constructor(@JsonProperty("num") val num: Double) : TermDTO {
    override fun toTerm(): Num {
        return Num(num)
    }
}
