package com.softwareleyline

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Created by Geoff on 5/1/2016.
 */
class ExampleCodeFixture {

    @Test fun when_branching_left_should_get_zero(){
        //setup
        val driver = Assig3Driver()
        val graph = buildGraph()

        //act
        val (pathID, pathProvider) = driver.determinePathFor(graph) {
            val instance = ExampleCode(true, true, true)
            instance.runDAG();
        }

        //assert
        assertThat(pathID).isEqualTo(2)
        assertThat(pathProvider()).isEqualTo(Path("A", "B", "C", "D", "E", "F"))
    }

    private fun buildGraph() : Node {
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