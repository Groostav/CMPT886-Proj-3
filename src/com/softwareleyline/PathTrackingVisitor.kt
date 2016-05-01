package com.softwareleyline

import java.util.*

class PathTrackingVisitor(private val pathCountByNode : Map<Node, Int>) : NodeVisitor() {

    private val _pathIDByNodePath: MutableMap<Path, Int> = LinkedHashMap();
    val pathIDByNodeNames : Map<Path, Int> get() = _pathIDByNodePath

    override fun enter(node: Node) {

        //think 'list of xpaths'
        var roots : List<Path> = if(_pathIDByNodePath.isEmpty()) {
            val rootPath = Path(node.name)
            _pathIDByNodePath[rootPath] = 0
            listOf(rootPath)
        }
        else _pathIDByNodePath.keys.filter { it.last() == node.name }

        if(roots.any{ it.first() != "A" }){
            val x = 4;
        }

        if(node.successors.any()) for(path in roots){
            val currentPathId = _pathIDByNodePath.remove(path)!!
            for(next in node.successors){
                val olderSibs = next.getOlderSiblingsBy(node)
                val newOffset = olderSibs.sumBy { pathCountByNode[it]!! }
                _pathIDByNodePath.put(path + next.name, currentPathId + newOffset)
            }
        }
    }

    override fun leave(node: Node) {
        //noop
    }
}