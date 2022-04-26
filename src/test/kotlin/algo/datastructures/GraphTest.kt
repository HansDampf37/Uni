package algo.datastructures

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import subsets

internal class GraphTest {
    @Test
    fun testK_n() {
        val k5 = Graph.kn(5)
        assertEquals(5, k5.v.size)
        assertEquals(10, k5.e.size)
    }

    @Test
    fun testK_n_m() {
        val k23 = Graph.knm(2, 3)
        assertEquals(5, k23.v.size)
        assertEquals(6, k23.e.size)
    }

    @Test
    fun testClique() {
        val k100 = Graph.kn(100)
        println(k100.e.size)
        assertTrue(k100.isClique())
    }

    @Test
    fun testOmega() {
        assertEquals(7, Graph.kn(7).omega())
        assertEquals(2, Graph.knm(4,3).omega())
        assertEquals(2, Graph.cn(4).omega())
        assertEquals(2, Graph.cn(5).omega())
        assertEquals(2, Graph.pn(4).omega())
        assertEquals(1, Graph.en(4).omega())
    }

    @Test
    fun testAlpha() {
        assertEquals(1, Graph.kn(7).alpha())
        assertEquals(4, Graph.knm(4,3).alpha())
        assertEquals(2, Graph.cn(4).alpha())
        assertEquals(2, Graph.cn(5).alpha())
        assertEquals(2, Graph.pn(4).alpha())
        assertEquals(4, Graph.en(4).alpha())
    }

    @Test
    fun testChi() {
        assertEquals(7, Graph.kn(7).chi())
        assertEquals(2, Graph.knm(4,3).chi())
        assertEquals(2, Graph.cn(4).chi())
        assertEquals(3, Graph.cn(5).chi())
        assertEquals(2, Graph.pn(4).chi())
        assertEquals(1, Graph.en(4).chi())
    }

    @Test
    fun testKappa() {
        assertEquals(1, Graph.kn(7).kappa())
        assertEquals(4, Graph.knm(4,3).kappa())
        assertEquals(2, Graph.cn(4).kappa())
        assertEquals(3, Graph.cn(5).kappa())
        assertEquals(2, Graph.pn(4).kappa())
        assertEquals(4, Graph.en(4).kappa())
    }

    @Test
    fun testInducedSubgraph() {
        val g = Graph.kn<Int, Any>(4) { it }
        val p = Graph.Path(g, g.v)
        println(p)
        val ind = p.inducedSubGraph(g.v.toMutableList().apply { removeAt(0) })
        assertEquals(3, ind.v.size)
        assertEquals(2, ind.e.size)
    }

    @Test
    fun testTimes() {
        val g = Graph.kn<Int, Any>(4) { it }
        val g1 = g * g.v[0]
        assertEquals(5, g1.v.size)
        assertEquals(9, g1.e.size)
    }
}