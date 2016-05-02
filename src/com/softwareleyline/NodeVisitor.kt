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

    open val predecessorsFirst = true;

    abstract fun enter(node : Node);
    abstract fun leave(node : Node);
}