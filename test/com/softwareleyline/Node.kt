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


class Node(val name : String, val signature : String) {

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

    val flattened : Set<Node>
        get() {
            //yaay recursive fields! neat.
            return setOf(this).union(predeccessors.flatMap { it.flattened })
        }

    fun getOlderSiblingsBy(parent: Node) : List<Node> {
        return parent.successors.run { subList(0, indexOf(parent)) }
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
        val totalPathsOut = node.successors.fold(1){ sum, node -> sum + pathCountByNode[node]!! }

        _pathCountByNode[node] = totalPathsOut

    }
}