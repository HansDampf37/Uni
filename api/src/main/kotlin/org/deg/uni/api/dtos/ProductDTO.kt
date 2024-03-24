package org.deg.uni.api.dtos

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.deg.uni.analysis.terms.model.Product

@JsonTypeName("product")
data class ProductDTO @JsonCreator constructor(@JsonProperty("factors") val factors: List<TermDTO>) : TermDTO {
    override fun toTerm(): Product {
        return Product(factors.map { it.toTerm() })
    }
}