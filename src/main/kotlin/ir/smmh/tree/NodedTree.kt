package ir.smmh.tree

interface NodedTree<DataType, NodeType : NodedTree.Node<DataType, NodeType>> : Tree<DataType> {
    interface Node<DataType, NodeType>
}