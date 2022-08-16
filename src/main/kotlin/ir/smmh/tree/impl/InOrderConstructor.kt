package ir.smmh.tree.impl

import ir.smmh.nile.Sequential
import ir.smmh.tree.Tree.Binary.OrderConstructor

class InOrderConstructor<DataType>(
    private val preOrder: Sequential<DataType>,
    private val postOrder: Sequential<DataType>
) : OrderConstructor<DataType> {
    private var tree: NodedBinaryTreeImpl<DataType>? = null
    private var size = 0
    private var preOrderIndex = 0
    override fun getFirstSource(): Sequential<DataType> {
        return preOrder
    }

    override fun getSecondSource(): Sequential<DataType> {
        return postOrder
    }

    override fun getTarget(): Sequential<DataType?> {
        return getTree().traverseDataInOrder()
    }

    override fun getTree(): NodedBinaryTreeImpl<DataType> {
        if (tree == null) {
            tree = NodedBinaryTreeImpl()
            size = preOrder.size
            assert(size == postOrder.size)
            preOrderIndex = 0
            tree!!.setRootNode(makeNode(0, size - 1, null))
        }
        return tree!!
    }

    private fun makeNode(
        start: Int,
        end: Int,
        parent: NodedBinaryTreeImpl<DataType>.Node?
    ): NodedBinaryTreeImpl<DataType>.Node? {
        return if (preOrderIndex < size && start <= end) {
            val node: NodedBinaryTreeImpl<DataType>.Node = tree!!.Node(preOrder.getAtIndex(preOrderIndex++), parent)
            if (preOrderIndex < size && start != end) {
                var m: Int = start
                while (m <= end) {
                    if (postOrder.getAtIndex(m) === preOrder.getAtIndex(preOrderIndex)) break
                    m++
                }
                if (m <= end) {
                    node.setLeftChild(makeNode(start, m, node))
                    node.setRightChild(makeNode(m + 1, end - 1, node))
                }
            }
            node
        } else {
            null
        }
    }
}