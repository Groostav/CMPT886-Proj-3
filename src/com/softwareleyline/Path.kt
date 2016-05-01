package com.softwareleyline

data class Path(val elements : List<String>) : List<String> by elements{

    constructor(vararg elems : String) : this(elems.toList())

    init{ if(elements.isEmpty()) throw IllegalArgumentException("elements") }

    override fun toString() = elements.fold("") { next, accum -> next + "/" + accum }

    infix operator fun plus(other : String) = Path(elements.plus(other))
}