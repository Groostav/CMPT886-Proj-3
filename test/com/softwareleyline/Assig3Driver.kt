package com.softwareleyline

import javassist.ClassPool
import javassist.CtClass
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Created by Geoff on 4/28/2016.
 */
class Assig3Driver {

    @Test fun doStuff(){
        val callGraph = buildGraph()
        val map = buildNodeMap(callGraph)

        val ctClass = rewriteByteCode(map, callGraph.flattened())

        val result = runTest(ctClass, a = true, b = true, d = true)

        assertThat(result).describedAs("the resulting Path ID").isEqualTo(1)
    }

    private fun runTest(provider : CtClass, a: Boolean, b: Boolean, d: Boolean) : Int {
        val ctor = provider.toClass().getConstructor(
                Boolean::class.java,
                Boolean::class.java,
                Boolean::class.java
        )

        val instance = ctor.newInstance(a, b, d) as ExampleCode

        instance.runDAG();

        return instance.pathID;
    }

    private fun buildGraph() : Node {
        val a = Node("A", "()Z")
        val b = Node("B", "()Z")
        val c = Node("C", "()V")
        val d = Node("D", "()Z")
        val e = Node("E", "()V")
        val f = Node("F", "()V")

        a.successors.addAll(listOf(b, c))
        b.predeccessors.add(a)
        b.successors.addAll(listOf(c, d))
        c.predeccessors.addAll(listOf(a, b))
        c.successors.add(d)
        d.predeccessors.addAll(listOf(b, c))
        d.successors.addAll(listOf(e, f))
        e.predeccessors.add(d)
        e.successors.add(f)
        f.predeccessors.addAll(listOf(d, e))

        return a;
    }

    // builds the path-count-to-exit-by-node map
    // assumes there is only one zero-successor node.
    // if there are multiple zero-successor nodes, all of them are assumed to be 'exits'.
    // not sure if this algorithm still works under that condition
    // intuition says it should for some, if not all, possible graphs.
    private fun buildNodeMap(root : Node) : Map<Node, Int>{
        val visitor = PathCountingVisitor()
        root.accept(visitor)
        return visitor.pathCountByNode;
    }

    private fun rewriteByteCode(nodeByPathCount : Map<Node, Int>, nodesToReWrite : Set<Node>) : CtClass {

        pathCountByNodeName = nodeByPathCount.mapKeys { it.key.name }
        nodeByName = nodesToReWrite.associateBy { it.name }
        //_god_ i love this language.

        val pool = ClassPool.getDefault();
        var clazz = pool.get("com.softwareleyline.ExampleCode")

        for(node in nodesToReWrite){
            val method = clazz.getMethod(node.name, node.signature)

            method.insertBefore("com.softwareleyline.Assig3DriverKt.hit(\"${node.name}\");")
        }

        clazz.freeze()

        return clazz
    }

}

var pathCountByNodeName : Map<String, Int> = emptyMap()
var nodeByName : Map<String, Node> = emptyMap();

var path = 0
var lastVisitedNode = ""

//well, insertBefore doesnt take a closure, annoyingly,
// so im reduced to this kind of singleton.
fun hit(name : String) {

    if(lastVisitedNode.isEmpty()){
        lastVisitedNode = name;
        return;
    }

    val olderSibs = nodeByName.get(name)!!.getOlderSiblingsBy(nodeByName.get(lastVisitedNode)!!)
    path += olderSibs.sumBy { pathCountByNodeName[it.name]!! }

    lastVisitedNode = name;

    return;
}