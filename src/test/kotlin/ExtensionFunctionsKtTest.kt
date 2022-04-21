import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ExtensionFunctionsKtTest {

    @Test
    fun factorial() {
        assertEquals(24L, 4.factorial())
        assertEquals(1L, 0.factorial())
    }

    @Test
    fun permute() {
        assertEquals(4 * 3 * 2, listOf(1, 2, 3, 4).permute().size)
    }

    @Test
    fun partition() {
        println(IntRange(1, 10).toList().partition(2).joinToString("\n") { it.toString() })
        println("\n" + IntRange(1, 10).toList().partition(3).joinToString("\n") { it.toString() })
        println("\n" + IntRange(1, 20).toList().partition(5).joinToString("\n") { it.toString() })

        assertEquals(9, IntRange(1, 10).toList().partition(2).size)
        assertEquals(36, IntRange(1, 10).toList().partition(3).size)
        assertEquals(3876, IntRange(1, 20).toList().partition(5).size)
    }
}