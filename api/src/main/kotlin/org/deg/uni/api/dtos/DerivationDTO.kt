package org.deg.uni.api.dtos

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class DerivationDTO @JsonCreator constructor(
    @JsonProperty("term") val term: TermDTO,
    @JsonProperty("variable") val variableName: String
)