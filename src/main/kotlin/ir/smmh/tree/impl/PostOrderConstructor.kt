package ir.smmh.tree.impl

import ir.smmh.nile.Sequential
import ir.smmh.tree.Tree.Binary.OrderConstructor

class PostOrderConstructor<DataType>(
    private val preOrder: Sequential<DataType>,
    private val inOrder: Sequential<DataType>
) : OrderConstructor<DataType> {
    private var tree: NodedBinaryTreeImpl<DataType>? = null
    private var preOrderIndex = 0
    override fun getFirstSource(): Sequential<DataType> {
        return preOrder
    }

    override fun getSecondSource(): Sequential<DataType> {
        return inOrder
    }

    override fun getTarget(): Sequential<DataType?> {
        return getTree().traverseDataPostOrder()
    }

    override fun getTree(): NodedBinaryTreeImpl<DataType> {
        if (tree == null) {
            tree = NodedBinaryTreeImpl()
            val n = preOrder.size
            assert(n == inOrder.size)
            preOrderIndex = 0
            tree!!.setRootNode(makeNode(0, n - 1, null))
        }
        return tree!!
    }

    private fun makeNode(
        start: Int,
        end: Int,
        parent: NodedBinaryTreeImpl<DataType>.Node?
    ): NodedBinaryTreeImpl<DataType>.Node? {
        if (start > end) return null
        val data = preOrder.getAtIndex(preOrderIndex++)
        val m = inOrder.findFirst(data, start, end + 1) // TODO optimize this search with a lookup
        val node: NodedBinaryTreeImpl<DataType>.Node = tree!!.Node(data, parent)
        node.setLeftChild(makeNode(start, m - 1, node))
        node.setRightChild(makeNode(m + 1, end, node))
        return node
    }
}