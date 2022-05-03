package org.deg.uni.graphs.datastructures

import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.geom.Point3
import org.graphstream.ui.spriteManager.SpriteManager
import org.graphstream.ui.view.Viewer
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.lang.Thread.sleep

class GraphViewer(private val g: Graph<*, *>) : KeyListener, Runnable, MouseListener {
    private var mouseAnchor: Point = Point(0, 0)
    private val nodeNames: HashMap<INode<*>, String> = HashMap()
    private val edgeNames: HashMap<Edge<*, *>, String> = HashMap()

    private val gDisplay = MultiGraph("")
    private var on = true
    private val viewer = display()
    private val view = viewer.defaultView
    private val cam = view.camera

    private var center = cam.viewCenter

    init {
        Thread(this).start()
        viewer.defaultView.addListener("Key", this)
        viewer.defaultView.addListener("Mouse", this)
    }

    override fun run() {
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
        if (keyMap[13]) {
            cam.resetView()
        }
    }

    private val keyMap = Array(600) { false }
    private val mouseMap = Array(10) { false }

    private val zoomIn = 16
    private val zoomOut = 32
    private val left = 65
    private val right = 68
    private val up = 87
    private val down = 83
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
        val url = this.javaClass.getResource("/myStyleNew.css")
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
}

fun main() {
    Graph.kn<Int, Any>(5) { it }.display()
}
