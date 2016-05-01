package com.softwareleyline

import javassist.ClassPool

/**
 * Created by Geoff on 4/28/2016.
 */
class Assig3Driver {

    /**
     * executes the call to profile with installed instrimentation as per the provided graph.
     *
     * note: the profiled
     */
    fun determinePathFor(callGraph: Node, callToProfile: () -> Unit) : Pair<Int, () -> Path> {
        val map = buildNodeMap(callGraph)

        val pathMap = generatePathIDByPathNodeNamesMap(map, callGraph);
        println("Path Id's are as follows:\n $pathMap")

        // note: from a brief test, ExampleCode::class.java does not appear to statically load example class.
        // unfortunately there isnt a `clazz.isLoaded` to assert on, so we'll just have to hope
        // we dont run in to static initializer problems.
        // read: beware of side-effects-galore.
        rewriteByteCode("com.softwareleyline.ExampleCode", map, callGraph.flattened())

        val result = runTest(callToProfile)

        val pathProvider : () -> Path = { pathMap.entries.single{ it.value == result }.key }
        return Pair(result, pathProvider);
    }

    private fun generatePathIDByPathNodeNamesMap(map: Map<Node, Int>, callGraph: Node) : Map<Path, Int> {
        val visitor = PathTrackingVisitor(map);
        callGraph.accept(visitor);
        return visitor.pathIDByNodeNames;
    }

    private fun runTest(method : () -> Unit) : Int {

        method();

        return path;
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

    private fun rewriteByteCode(target : String,
                                nodeByPathCount : Map<Node, Int>,
                                nodesToReWrite : Set<Node>){

        pathCountByNodeName = nodeByPathCount.mapKeys { it.key.name }
        nodeByName = nodesToReWrite.associateBy { it.name }

        val pool = ClassPool.getDefault();
        var clazz = pool.get(target)

        for(node in nodesToReWrite){
            val method = clazz.getMethod(node.name, node.signature)

            method.insertBefore("com.softwareleyline.Assig3DriverKt.hit(\"${node.name}\");")
        }

        clazz.freeze()
        val targetDir = javaClass.protectionDomain.codeSource.location.path
        clazz.writeFile(targetDir.toString())
    }

}

//TODO this isn't thread safe.
// could get some thread safety by using THreadLocal,
// but that blows up if the annotated methods do their own thread hoping.
// could add a UUID to the static closure, a little yucky but would do the job.

var pathCountByNodeName : Map<String, Int> = emptyMap()
var nodeByName : Map<String, Node> = emptyMap();

var path = 0
var lastVisitedNode = ""

private fun reset(){
    path = 0;
    lastVisitedNode = "";
    pathCountByNodeName = emptyMap();
    nodeByName = emptyMap();
}

//well, insertBefore doesnt take a closure, annoyingly,
// so im reduced to this kind of singleton.
@Suppress("unused") // called via byte-code rewrite
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