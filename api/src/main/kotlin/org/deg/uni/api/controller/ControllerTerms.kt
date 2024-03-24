package org.deg.uni.api.controller

import org.deg.uni.analysis.terms.model.Variable
import org.deg.uni.analysis.terms.model.simplify
import org.deg.uni.analysis.terms.model.toTerm
import org.deg.uni.api.dtos.DerivationDTO
import org.deg.uni.api.dtos.TermDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/term")
class ControllerTerms {
    @GetMapping("/simplify")
    fun simplifyEquation(
        @RequestBody term: TermDTO,
    ): TermDTO {
        return TermDTO.fromTerm(term.toTerm().simplify(false))
    }

    @GetMapping("/derive/{variableName}")
    fun deriveEquation(
        @RequestBody derivation: DerivationDTO
    ): TermDTO {
        return TermDTO.fromTerm(derivation.term.toTerm().derive(Variable(derivation.variableName)))
    }

    @GetMapping("/parse")
    fun parseEquation(
        @RequestBody term: String,
    ): TermDTO {
        return TermDTO.fromTerm(term.toTerm())
    }
}