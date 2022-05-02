package org.deg.uni.unification

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PermutationsTest {

    @Test
    operator fun iterator() {
        val permutations = Permutations(listOf(1, 2, 3, 4))
        println(permutations.map { it.toString() })
        assertEquals(24, permutations.toList().size)
        assertTrue(permutations.toList().indices.all { i -> permutations.toList().count { it == permutations.toList()[i] } == 1 })
    }
}