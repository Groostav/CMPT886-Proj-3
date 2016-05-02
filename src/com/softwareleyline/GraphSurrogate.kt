package com.softwareleyline

/**
 * Created by Geoff on 5/2/2016.
 */
data class GraphSurrogate(
        val nodes : List<NodeSurrogate>
){
    fun asGraph() : Node {

        val partials = nodes.map(NodeSurrogate::toPartial);

        val nodes = partials.map{ it.first }

        for((node, preds, sucs) in partials){
            for(successorName in sucs){
                node.successors.add(nodes.single{ it.name == successorName })
            }

            for(predecessorName in preds){
                node.predeccessors.add(nodes.single{ it.name == predecessorName })
            }
        }

        var root = nodes.first();

        while(root.predeccessors.any()){ root = root.predeccessors.first() }

        return root;
    }
}

fun Node.toSurrogate() : GraphSurrogate{

    val surrogates = flattened().map { it.run { NodeSurrogate(
            "",
            name,
            signature,
            predeccessors.map{ it.name },
            successors.map{ it.name })
    }}

    return GraphSurrogate(surrogates)
}

data class NodeSurrogate (
    val className : String,
    val methodName : String,
    val signature : String,
    val predecessors : List<String>,
    val successors : List<String>
){
    fun toPartial() : Triple<Node, List<String>, List<String>>{
        val node = Node(methodName, signature)

        return Triple(node, predecessors, successors)
    }
}
