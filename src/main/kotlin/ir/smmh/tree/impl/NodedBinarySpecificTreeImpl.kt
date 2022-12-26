package ir.smmh.tree.impl

import ir.smmh.nile.DoubleSequence
import ir.smmh.nile.Change
import ir.smmh.nile.Sequential
import ir.smmh.nile.verbs.CanContainValue
import ir.smmh.tree.NodedSpecificTree


class NodedBinarySpecificTreeImpl<DataType>(
    override val changesToValues: Change = Change(),
    override val changesToSize: Change = Change(), // TODO add appropriate usages
) :
    NodedSpecificTree.Binary.Mutable<DataType, NodedBinarySpecificTreeImpl<DataType>.Node, NodedBinarySpecificTreeImpl<DataType>> {
    override var rootNode: Node? = null
    private val nodeContainer: CanContainValue<Node> = object : CanContainValue<Node> {
        override fun containsValue(toCheck: NodedBinarySpecificTreeImpl<DataType>.Node): Boolean {
            val r = this@NodedBinarySpecificTreeImpl.rootNode
            return r != null && this@NodedBinarySpecificTreeImpl.contains(r, toCheck)
        }

        override fun isEmpty() = this@NodedBinarySpecificTreeImpl.isEmpty()
        override val size: Int get() = this@NodedBinarySpecificTreeImpl.size
    }

    override fun toString() = this.rootNode?.nodeToString() ?: "{empty}"

    override fun containsValue(toCheck: DataType): Boolean {
        val r = this.rootNode
        return r != null && contains(r, toCheck)
    }

    private fun contains(root: Node, data: DataType): Boolean {
        if (root.data == data) return true
        for (child in root.children) {
            if (child != null) {
                if (contains(child, data)) return true
            }
        }
        return false
    }

    private fun contains(root: Node, node: Node): Boolean {
        if (root == node) return true
        for (child in root.children) {
            if (child != null) {
                if (contains(child, node)) return true
            }
        }
        return false
    }

    override fun isEmpty(): Boolean {
        return this.rootNode == null
    }

    override fun nodes(): CanContainValue<Node> {
        return nodeContainer
    }

    override var rootData: DataType?
        get() = this.rootNode?.data
        set(value) {
            rootNode = if (value == null) null else Node(value, null)
        }

    override fun specificThis(): NodedBinarySpecificTreeImpl<DataType> {
        return this
    }

    inner class Node(override var data: DataType, parent: Node?) :
        NodedSpecificTree.Binary.Mutable.Node<DataType, Node, NodedBinarySpecificTreeImpl<DataType>> {
        override var leftChild: Node? = null
        override var rightChild: Node? = null
        override var parent: Node? = parent
        override fun toString(): String {
            return "<$data>[$leftChild|$rightChild]"
        }

        internal fun nodeToString(): String {
            val l: Node? = leftChild
            val r: Node? = rightChild
            var s: String = data?.toString() ?: "~"
            if (l != null || r != null) s += ":(" +
                    (l?.nodeToString() ?: "-") + ", " +
                    (r?.nodeToString() ?: "-") + ")"
            return s
        }

        override fun asTree(): NodedBinarySpecificTreeImpl<DataType> {
            val subtree = NodedBinarySpecificTreeImpl<DataType>()
            subtree.rootNode = this
            // TODO handle mutation and/or viewing
            return subtree
        }

        override val children: Sequential<Node?>
            get() = DoubleSequence(leftChild, rightChild)

        override val indexInParent: Int
            get() {
                val p = parent
                return if (p == null) -1 else when (this) {
                    p.leftChild -> 0
                    p.rightChild -> 1
                    else -> -1
                }
            }

        override val tree: NodedBinarySpecificTreeImpl<DataType>
            get() {
                return this@NodedBinarySpecificTreeImpl
            }

        override fun specificThis(): Node {
            return this
        }
    }
}