package com.softwareleyline

object SampleGraph{

    fun buildGraph() : Node {
        val a = Node("A", "()Z") //yeah, "Z" is JVM speak for boolean, weird. B was byte so...
        val b = Node("B", "()Z")
        val c = Node("C", "()V")
        val d = Node("D", "()Z")
        val e = Node("E", "()V")
        val f = Node("F", "()V")

        a.successors.addAll(listOf(b, c))
        b.predeccessors.add(a)
        b.successors.addAll(listOf(d, c))
        c.predeccessors.addAll(listOf(a, b))
        c.successors.add(d)
        d.predeccessors.addAll(listOf(b, c))
        d.successors.addAll(listOf(e, f))
        e.predeccessors.add(d)
        e.successors.add(f)
        f.predeccessors.addAll(listOf(d, e))

        return a;
    }
}