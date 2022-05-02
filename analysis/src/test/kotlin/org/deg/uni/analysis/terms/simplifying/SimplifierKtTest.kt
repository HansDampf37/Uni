package org.deg.uni.analysis.terms.simplifying

import org.deg.uni.analysis.terms.model.Num
import org.deg.uni.analysis.terms.model.Sum
import org.deg.uni.analysis.terms.x
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.withSign

internal class SimplifierKtTest {
    @Test
    fun testQuality() {
        assertEquals(0.0, x.quality().withSign(1))
        assertEquals(0.0, Num(1).quality().withSign(1))
        assertEquals(0.0, Num(-200).quality().withSign(1))
        assertTrue(0.0 >= Sum(x, x).quality())
    }
}