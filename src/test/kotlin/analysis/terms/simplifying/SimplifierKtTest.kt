package analysis.terms.simplifying

import analysis.terms.Num
import analysis.terms.Sum
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SimplifierKtTest {
    @Test
    fun testQuality() {
        assertEquals(-0.0, x.quality())
        assertEquals(-0.0, Num(1).quality())
        assertEquals(-0.0, Num(-200).quality())
        assertTrue(0.0 >= Sum(x, x).quality())
    }
}