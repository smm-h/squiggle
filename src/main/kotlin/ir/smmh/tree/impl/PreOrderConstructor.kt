package ir.smmh.tree.impl

import ir.smmh.nile.Sequential
import ir.smmh.tree.Tree

class PreOrderConstructor<DataType>(
    private val inOrder: Sequential<DataType>,
    private val postOrder: Sequential<DataType>
) : Tree.Binary.OrderConstructor<DataType> {
    private var tree: NodedBinarySpecificTreeImpl<DataType>? = null
    override fun getFirstSource(): Sequential<DataType> {
        return inOrder
    }

    override fun getSecondSource(): Sequential<DataType> {
        return postOrder
    }

    override fun getTarget(): Sequential<DataType?> {
        return getTree().traverseDataPreOrder()
    }

    override fun getTree(): NodedBinarySpecificTreeImpl<DataType> {
        if (tree == null) {
            tree = NodedBinarySpecificTreeImpl()
            val n = inOrder.size
            assert(n == postOrder.size)
            tree!!.rootNode = makeNode(0, n - 1, 0, n - 1, null)
        }
        return tree!!
    }

    private fun makeNode(
        inOrderFirst: Int,
        inOrderLast: Int,
        postOrderFirst: Int,
        postOrderLast: Int,
        parent: NodedBinarySpecificTreeImpl<DataType>.Node?
    ): NodedBinarySpecificTreeImpl<DataType>.Node? {

        // base case
        if (inOrderFirst > inOrderLast) return null
        val data = postOrder.getAtIndex(postOrderLast)
        val node: NodedBinarySpecificTreeImpl<DataType>.Node = tree!!.Node(data, parent)

        // if this node has children
        if (inOrderFirst != inOrderLast) {

            // find the index in in-order traversal
            val index = inOrder.findFirst(data, inOrderFirst, inOrderLast + 1)

            // and use it to construct both subtrees
            node.leftChild = makeNode(
                inOrderFirst,
                index - 1,
                postOrderFirst,
                postOrderFirst - inOrderFirst + index - 1,
                node
            )
            node.rightChild = makeNode(
                index + 1,
                inOrderLast,
                postOrderLast - inOrderLast + index,
                postOrderLast - 1,
                node
            )
        }
        return node
    }
}