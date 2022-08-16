package ir.smmh.tree.impl

import ir.smmh.nile.DoubleSequence
import ir.smmh.nile.Mut
import ir.smmh.nile.Sequential
import ir.smmh.nile.verbs.CanContainValue
import ir.smmh.tree.NodedTree


class NodedBinaryTreeImpl<DataType>(override val mut: Mut = Mut()) :
    NodedTree.Binary.Mutable<DataType, NodedBinaryTreeImpl<DataType>.Node, NodedBinaryTreeImpl<DataType>> {
    private var root: Node? = null
    private val nodeContainer: CanContainValue<Node> = object : CanContainValue<Node> {
        override fun containsValue(toCheck: NodedBinaryTreeImpl<DataType>.Node): Boolean {
            val r = root
            return r != null && this@NodedBinaryTreeImpl.contains(r, toCheck)
        }

        override fun isEmpty() = this@NodedBinaryTreeImpl.isEmpty()
        override val size: Int get() = this@NodedBinaryTreeImpl.size
    }

    override fun toString() = root?.nodeToString() ?: "{empty}"

    override fun containsValue(toCheck: DataType): Boolean {
        val r = root
        return r != null && contains(r, toCheck)
    }

    private fun contains(root: Node, data: DataType): Boolean {
        if (root.getData() == data) return true
        for (child in root.getChildren()) {
            if (child != null) {
                if (contains(child, data)) return true
            }
        }
        return false
    }

    private fun contains(root: Node, node: Node): Boolean {
        if (root == node) return true
        for (child in root.getChildren()) {
            if (child != null) {
                if (contains(child, node)) return true
            }
        }
        return false
    }

    override fun isEmpty(): Boolean {
        return root == null
    }

    override fun nodes(): CanContainValue<Node> {
        return nodeContainer
    }

    override fun getRootNode(): Node? {
        return root
    }

    override fun setRootNode(node: Node?) {
        root = node
    }

    override fun getRootData(): DataType? = root?.getData()

    override fun setRootData(data: DataType) {
        setRootNode(Node(data, null))
    }

    override fun specificThis(): NodedBinaryTreeImpl<DataType> {
        return this
    }

    inner class Node(private var data: DataType, parent: Node?) :
        NodedTree.Binary.Mutable.Node<DataType, Node, NodedBinaryTreeImpl<DataType>> {
        private var leftChild: Node? = null
        private var rightChild: Node? = null
        private var parent: Node?
        override fun toString(): String {
            return "<$data>[$leftChild|$rightChild]"
        }

        internal fun nodeToString(): String {
            val l: Node? = getLeftChild()
            val r: Node? = getRightChild()
            var s: String = getData()?.toString() ?: "~"
            if (l != null || r != null) s += ":(" +
                    (l?.nodeToString() ?: "-") + ", " +
                    (r?.nodeToString() ?: "-") + ")"
            return s
        }

        override fun asTree(): NodedBinaryTreeImpl<DataType> {
            val subtree = NodedBinaryTreeImpl<DataType>()
            subtree.setRootNode(this)
            // handle mutation and/or viewing
            return subtree
        }

        override fun getChildren(): Sequential<Node?> {
            return DoubleSequence(leftChild, rightChild)
        }

        override fun getIndexInParent(): Int {
            val p = parent
            if (p != null) {
                if (this === p.leftChild) return 0
                if (this === p.rightChild) return 1
            }
            return -1
        }

        override fun getData(): DataType {
            return data
        }

        override fun setData(data: DataType) {
            this.data = data
        }

        override fun getParent(): Node? {
            return parent
        }

        override fun setParent(parent: Node?) {
            this.parent = parent
        }

        override fun getTree(): NodedBinaryTreeImpl<DataType> {
            return this@NodedBinaryTreeImpl
        }

        override fun specificThis(): Node {
            return this
        }

        override fun getLeftChild(): Node? {
            return leftChild
        }

        override fun setLeftChild(leftChild: Node?) {
            this.leftChild = leftChild
        }

        override fun getRightChild(): Node? {
            return rightChild
        }

        override fun setRightChild(rightChild: Node?) {
            this.rightChild = rightChild
        }

        init {
            this.parent = parent
        }
    }
}