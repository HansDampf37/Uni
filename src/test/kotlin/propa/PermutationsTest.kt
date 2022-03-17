package propa

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PermutationsTest {

    @Test
    operator fun iterator() {
        val permutations = Permutations(listOf(1, 2, 3, 4))
        println(permutations.map { it.toString() })
        assertEquals(24, permutations.toList().size)
    }
}