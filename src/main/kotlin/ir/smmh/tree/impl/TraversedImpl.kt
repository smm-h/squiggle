package ir.smmh.tree.impl

import ir.smmh.nile.Sequential
import ir.smmh.tree.NodedSpecificTree
import ir.smmh.tree.NodedSpecificTree.Traversal
import ir.smmh.tree.NodedSpecificTree.Traversed

class TraversedImpl<DataType, NodeType : NodedSpecificTree.Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>>(
    override val nodes: Sequential<NodeType?>,
    override val type: Traversal<DataType, NodeType, TreeType>
) : Traversed<DataType, NodeType, TreeType> {
    override val data: Sequential<DataType?> = nodes.applyOutOfPlace { it?.data }
    override fun toString() = nodes.toString()
}