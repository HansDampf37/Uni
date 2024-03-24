package org.deg.uni.api.dtos

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.deg.uni.analysis.terms.model.Variable

@JsonTypeName("variable")
data class VariableDTO @JsonCreator constructor(@JsonProperty("name") val name: String): TermDTO {
    override fun toTerm(): Variable {
        return Variable(name)
    }
}