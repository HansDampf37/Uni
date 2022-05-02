package org.deg.uni.analysis.terms.simplifying

import org.deg.uni.analysis.terms.four
import org.deg.uni.analysis.terms.two
import org.deg.uni.analysis.terms.x
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class QualityTest {
    @Test
    fun testQuality1() {
        val term1 = P(P(Pow(x, two), two), two)
        val term2 = P(Pow(x, two), four)
        println("term 1: ${term1.quality()}")
        println("term 2: ${term2.quality()}")
        assertTrue(term1.quality() < term2.quality())
    }
}