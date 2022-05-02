package org.deg.uni.analysis.terms.simplifying

import org.deg.uni.analysis.terms.model.Log
import org.deg.uni.analysis.terms.model.Variable
import org.deg.uni.graphs.datastructures.DFS
import org.deg.uni.graphs.datastructures.ITree
import kotlin.math.sqrt

interface ITreeQuality {
    fun <T> calc(t: ITree<T>): Double
}

class TreeQuality : ITreeQuality {
    override fun <T> calc(t: ITree<T>): Double {
        if (t.root.isLeaf()) return 0.0
        val allNodes = DFS(t).toList()
        val amountOfVariables = allNodes.filterIsInstance<Variable>().size
        val amountOfLogs = allNodes.filterIsInstance<Log>().size
        val punishment: Double = weightDepth * t.depth() +
                weightSize * t.root.nodeSize() +
                weightAmountOfLeaves * t.width() +
                weightAmountOfVariable * amountOfVariables +
                weightAmountOfLogs * amountOfLogs
        return -sqrt(punishment)
    }

    companion object {
        const val weightDepth = 0.1
        const val weightSize = 0.1
        const val weightAmountOfVariable = 0.4
        const val weightAmountOfLogs = 0.25
        const val weightAmountOfLeaves = 1 - weightDepth - weightSize - weightAmountOfVariable
    }
}

class TreeQuality2: ITreeQuality {
    override fun <T> calc(t: ITree<T>): Double {
        if (t.root.subNodes().isEmpty()) return 0.0
        val subQualities = t.root.subNodes().map { calc(it.toTree()) }
        val width = t.root.subNodes().size
        val variables = t.root.subNodes().filterIsInstance<Variable>().size
        val logs = if (t is Log) 1 else 0
        return depthMultiplication * (0.5 * subQualities.sum() - widthMultiplication * width - weightAmountOfVariable * variables - weightAmountOfLogs * logs)
    }

    companion object {
        const val depthMultiplication = 2
        const val widthMultiplication = 0.2
        const val weightAmountOfVariable = 0.5
        const val weightAmountOfLogs = 0.3
    }
}