package ir.smmh.tree.impl

import ir.smmh.nile.Sequential
import ir.smmh.tree.NodedTree
import ir.smmh.tree.NodedTree.Traversal
import ir.smmh.tree.NodedTree.Traversed

class TraversedImpl<DataType, NodeType : NodedTree.Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>>(
    override val nodes: Sequential<NodeType?>,
    override val type: Traversal<DataType, NodeType, TreeType>
) : Traversed<DataType, NodeType, TreeType> {
    override val data: Sequential<DataType?> = nodes.applyOutOfPlace { it?.getData() }
    override fun toString() = nodes.toString()
}