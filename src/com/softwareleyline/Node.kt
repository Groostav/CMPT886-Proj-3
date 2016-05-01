package com.softwareleyline

import java.util.*

/**
 * Created by Geoff on 4/28/2016.
 */


abstract class NodeVisitor{

    private val visited : MutableList<Node> = ArrayList()

    final fun saw(node : Node) : Boolean{
        return visited.contains(node)
    }

    final fun markAsSeen(node : Node){
        visited.add(node);
    }

    abstract fun enter(node : Node);
    abstract fun leave(node : Node);
}


data class Node(val name : String, val signature : String) {

    val successors : MutableList<Node> = ArrayList()
    val predeccessors : MutableList<Node> = ArrayList()

    fun accept(visitor : NodeVisitor){

        if(predeccessors.any { ! visitor.saw(it) } ){
            return;
        }

        visitor.markAsSeen(this);

        visitor.enter(this);

        successors.forEach { it.accept(visitor) }

        visitor.leave(this);
    }

    fun flattened() : Set<Node> {
        //yaay recursive fields! neat.
        return setOf(this).union(successors.flatMap { it.flattened() })
    }

    fun getOlderSiblingsBy(parentName : String) : List<Node> {
        return getOlderSiblingsBy(predeccessors.single { it.name == parentName })
    }

    fun getOlderSiblingsBy(parent: Node) : List<Node> {
        assert(this in parent.successors) { "$parent is not a predecessor to $this" }
        return parent.successors.run { subList(0, indexOf(this@Node)) }
    }

}

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