package org.deg.uni.graphs.datastructures

import org.graphstream.algorithm.AStar
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.geom.Point3
import org.graphstream.ui.view.Viewer
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.lang.Thread.sleep

/**
 * Graphs can be shown in a window using this classes [run] method.
 *
 * @property g
 * @constructor Create empty Graph viewer
 */
class GraphViewer(private val g: Graph<*, *>) : KeyListener, Runnable, MouseListener {
    private var mouseAnchor: Point = Point(0, 0)
    private val nodeNames: HashMap<INode<*>, String> = HashMap()
    private val edgeNames: HashMap<Edge<*, *>, String> = HashMap()

    private val gDisplay = MultiGraph("")
    private var on = false
    private val viewer = display()
    private val view = viewer.defaultView
    private val cam = view.camera

    private var center = cam.viewCenter

    private val keyMap = Array(600) { false }
    private val mouseMap = Array(10) { false }

    // shift
    private val zoomIn = 16
    // space
    private val zoomOut = 32
    //a
    private val left = 65
    //d
    private val right = 68
    // w
    private val up = 87
    // s
    private val down = 83
    // enter
    private val resetView = 13
    // escape
    private val exit = 27

    init {
        Thread(this).start()
        viewer.defaultView.addListener("Key", this)
        viewer.defaultView.addListener("Mouse", this)
    }

    override fun run() {
        on = true
        while (on) {
            sleep(25)
            control()
            cam.viewPercent
            cam.setViewCenter(center.x, center.y, center.z)
            adapt()
        }
    }

    fun adapt() {
        val e = g.e
        val v = g.v
        for (node in v) {
            if (nodeNames[node] == null) {
                // node is not in gDisplay
                nodeNames[node] = nodeNames.keys.size.toString()
                addDisplayNode(node, nodeNames[node]!!)
            }
        }
        for (edge in e) {
            if (edgeNames[edge] == null) {
                // node is not in gDisplay
                edgeNames[edge] = edgeNames.keys.size.toString()
                addDisplayEdge(edge, edgeNames[edge]!!)
            }
        }
    }

    fun setAttributeForNode(node: INode<*>, attr: String, value: Any) {
        adapt()
        gDisplay.getNode(nodeNames[node]).setAttribute(attr, value)
    }

    fun setAttributeForEdge(edge: Edge<*, *>, attr: String, value: Any) {
        adapt()
        gDisplay.getEdge(edgeNames[edge]).setAttribute(attr, value)
    }

    private fun control() {
        if (keyMap[zoomIn]) {
            cam.viewPercent *= 0.96
        }
        if (keyMap[zoomOut]) {
            cam.viewPercent *= 1.14
        }
        if (keyMap[left]) {
            center = Point3(center.x - cam.viewPercent / 10, center.y, center.z)
        }
        if (keyMap[right]) {
            center = Point3(center.x + cam.viewPercent / 10, center.y, center.z)
        }
        if (keyMap[up]) {
            center = Point3(center.x, center.y + cam.viewPercent / 10, center.z)
        }
        if (keyMap[down]) {
            center = Point3(center.x, center.y - cam.viewPercent / 10, center.z)
        }
        if (keyMap[resetView]) {
            cam.resetView()
        }
        if (keyMap[exit]) {
            on = false
        }
    }

    override fun keyTyped(e: KeyEvent?) = Unit

    override fun keyPressed(e: KeyEvent?) {
        keyMap[e!!.keyCode] = true
    }

    override fun keyReleased(e: KeyEvent?) {
        keyMap[e!!.keyCode] = false
    }

    override fun mouseClicked(e: MouseEvent?) = Unit

    override fun mousePressed(e: MouseEvent?) {
        if (e!!.button == MouseEvent.BUTTON1) {
            mouseAnchor = e.locationOnScreen
        }
        println("button ${e.button} pressed")
        mouseMap[e.button] = true
    }

    override fun mouseReleased(e: MouseEvent?) {
        mouseMap[e!!.button] = false
    }

    override fun mouseEntered(e: MouseEvent?) = Unit

    override fun mouseExited(e: MouseEvent?) = Unit

    private fun display(): Viewer {
        System.setProperty("org.graphstream.ui", "swing")
        gDisplay.setAttribute("ui.quality")
        gDisplay.setAttribute("ui.antialias")
        val url = this::class.java.getResource("myStyleNew.css")
        if (url != null) {
            gDisplay.setAttribute("ui.stylesheet", "url(${url.path});")
        }
        for (i in g.v.indices) {
            val node = g.v[i]
            addDisplayNode(node, i.toString())
        }
        for (i in g.e.indices) {
            val edge = g.e[i]
            edgeNames[edge] = i.toString()
            gDisplay.addEdge(i.toString(), nodeNames[edge.from], nodeNames[edge.to], true)
            if (edge.el != null) gDisplay.getEdge(i.toString()).setAttribute("ui.label", edge.el.toString())
        }
        return gDisplay.display()
    }

    private fun addDisplayNode(node: INode<*>, id: String) {
        nodeNames[node] = id
        gDisplay.addNode(id)
        /*val uiLabel = "Node: ${node.element() ?: id}" + g.e.filter { it.from === node }.joinToString(", ") {
            "Neighbor: \"" + it.to.toString() + "\", Edge: \"" + it.el.toString() + "\""
        }*/
        val uiLabel = (node.element() ?: id).toString()
        if (node.element() != null) gDisplay.getNode(id).setAttribute("ui.label", uiLabel)
    }

    private fun addDisplayEdge(edge: Edge<*, *>, id: String) {
        edgeNames[edge] = id
        gDisplay.addEdge(id, nodeNames[edge.from], nodeNames[edge.to], true)
        if (edge.el != null) gDisplay.getEdge(id).setAttribute("ui.label", edge.el.toString())
    }

    fun showShortestPathFrom(start: INode<*>, best: INode<*>) {
        if (start === best) return
        val astar = AStar(gDisplay)
        astar.setCosts(AStar.DistanceCosts())
        astar.setSource(nodeNames[start])
        astar.setTarget(nodeNames[best])
        astar.compute()
        val p: org.graphstream.graph.Path = astar.shortestPath
        val edges = p.edgePath
        edges.forEach { it.setAttribute("ui.class", "shortestPath") }
    }
}

fun main() {
    Graph.kn<Int, Any>(5) { it }.display()
}
