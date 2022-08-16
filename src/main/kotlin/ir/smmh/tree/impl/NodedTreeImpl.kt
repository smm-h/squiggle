@file:Suppress("unused")

package ir.smmh.tree.impl

import ir.smmh.nile.Mut
import ir.smmh.nile.Sequential
import ir.smmh.nile.SequentialImpl
import ir.smmh.nile.verbs.CanContainValue
import ir.smmh.tree.NodedTree
import java.util.*


class NodedTreeImpl<DataType>(override val mut: Mut = Mut()) :
    NodedTree.Mutable<DataType, NodedTreeImpl<DataType>.Node, NodedTreeImpl<DataType>> {
    private var root: Node? = null

    private val nodeContainer: CanContainValue<Node> = object : CanContainValue<Node> {
        override fun containsValue(toCheck: NodedTreeImpl<DataType>.Node) = this@NodedTreeImpl.contains(root, toCheck)
        override fun isEmpty() = this@NodedTreeImpl.isEmpty()
        override val size: Int get() = this@NodedTreeImpl.size
    }

    override fun containsValue(toCheck: DataType) = contains(root, toCheck)

    private fun contains(root: Node?, data: DataType?): Boolean {
        if (root == null) return false
        if (root.getData() == data) return true
        for (child in root.getChildren()) {
            if (contains(child, data)) return true
        }
        return false
    }

    private fun contains(root: Node?, node: Node): Boolean {
        if (root == null) return false
        if (root == node) return true
        for (child in root.getChildren()) {
            if (contains(child, node)) return true
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

    override fun getRootData(): DataType? {
        return root?.getData()
    }

    override fun setRootData(data: DataType) {
        setRootNode(Node(data, null))
    }

    override fun specificThis(): NodedTreeImpl<DataType> {
        return this
    }

    override fun toString() = root?.nodeToString() ?: "{empty}"

    inner class Node(private var data: DataType, parent: Node?) :
        NodedTree.Mutable.Node<DataType, Node, NodedTreeImpl<DataType>> {
        // TODO Tree.VariableDegree
        private val children: Sequential.Mutable.VariableSize<Node?> = SequentialImpl()
        private var parent: Node?
        override fun toString(): String {
            return "<" + data + ">" + if (children.isEmpty()) "" else children.toString()
        }

        internal fun nodeToString(): String {
            var childrenString = ""
            if (!children.isEmpty()) {
                val joiner = StringJoiner(", ", ":(", ")")
                for (child in children) {
                    joiner.add(child?.nodeToString() ?: "-")
                }
                childrenString = joiner.toString()
            }
            return (data?.toString() ?: "~") + childrenString
        }

        private fun makeNode(data: DataType): Node {
            return Node(data, this)
        }

        override fun asTree(): NodedTreeImpl<DataType> {
            val subtree = NodedTreeImpl<DataType>()
            subtree.setRootNode(this)
            // handle mutation and/or viewing
            return subtree
        }

        override fun getChildren(): Sequential<Node?> {
            return children
        }

        override fun getIndexInParent(): Int {
            return parent?.children?.findFirst(this) ?: -1
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

        override fun getTree(): NodedTreeImpl<DataType> {
            return this@NodedTreeImpl
        }

        override fun specificThis(): Node {
            return this
        }

        init {
            this.parent = parent
        }
    }
}