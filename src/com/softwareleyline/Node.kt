package com.softwareleyline

import java.util.*


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

