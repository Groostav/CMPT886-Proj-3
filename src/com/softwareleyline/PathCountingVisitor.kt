package com.softwareleyline

import java.util.*

class PathCountingVisitor : NodeVisitor() {

    //uhh, debating adding validation to the graph, but too much work

    private val _pathCountByNode = HashMap<Node, Int>()

    val pathCountByNode : Map<Node, Int>
        get() = _pathCountByNode

    override fun enter(node: Node) {
        //do nothing
    }

    override fun leave(node: Node) {
        val totalPathsOut = node.successors.sumBy{ pathCountByNode[it]!! }.coerceAtLeast(1)

        _pathCountByNode[node] = totalPathsOut

    }
}