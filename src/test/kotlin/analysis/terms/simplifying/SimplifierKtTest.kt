package analysis.terms.simplifying

import analysis.terms.Num
import analysis.terms.Sum
import analysis.terms.x
import org.junit.jupiter.api.Test
import kotlin.math.withSign
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SimplifierKtTest {
    @Test
    fun testQuality() {
        assertEquals(0.0, x.quality().withSign(1))
        assertEquals(0.0, Num(1).quality().withSign(1))
        assertEquals(0.0, Num(-200).quality().withSign(1))
        assertTrue(0.0 >= Sum(x, x).quality())
    }
}