package org.deg.uni.api.dtos

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.deg.uni.analysis.terms.model.Power

@JsonTypeName("power")
data class PowerDTO @JsonCreator constructor(
    @JsonProperty("base") val base: TermDTO,
    @JsonProperty("exponent") val exponent: TermDTO
) : TermDTO {
    override fun toTerm(): Power {
        return Power(base.toTerm(), exponent.toTerm())
    }
}
