package ir.smmh.tree.impl

import ir.smmh.nile.Sequential
import ir.smmh.tree.Tree.Binary.OrderConstructor

class InOrderConstructor<DataType>(
    private val preOrder: Sequential<DataType>,
    private val postOrder: Sequential<DataType>
) : OrderConstructor<DataType> {
    private var tree: NodedBinarySpecificTreeImpl<DataType>? = null
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

    override fun getTree(): NodedBinarySpecificTreeImpl<DataType> {
        if (tree == null) {
            tree = NodedBinarySpecificTreeImpl()
            size = preOrder.size
            assert(size == postOrder.size)
            preOrderIndex = 0
            tree!!.rootNode = makeNode(0, size - 1, null)
        }
        return tree!!
    }

    private fun makeNode(
        start: Int,
        end: Int,
        parent: NodedBinarySpecificTreeImpl<DataType>.Node?
    ): NodedBinarySpecificTreeImpl<DataType>.Node? {
        return if (preOrderIndex < size && start <= end) {
            val node: NodedBinarySpecificTreeImpl<DataType>.Node =
                tree!!.Node(preOrder.getAtIndex(preOrderIndex++), parent)
            if (preOrderIndex < size && start != end) {
                var m: Int = start
                while (m <= end) {
                    if (postOrder.getAtIndex(m) === preOrder.getAtIndex(preOrderIndex)) break
                    m++
                }
                if (m <= end) {
                    node.leftChild = makeNode(start, m, node)
                    node.rightChild = makeNode(m + 1, end - 1, node)
                }
            }
            node
        } else {
            null
        }
    }
}