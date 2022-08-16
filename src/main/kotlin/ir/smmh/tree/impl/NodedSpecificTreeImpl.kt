@file:Suppress("unused")

package ir.smmh.tree.impl

import ir.smmh.nile.Mut
import ir.smmh.nile.Sequential
import ir.smmh.nile.SequentialImpl
import ir.smmh.nile.verbs.CanContainValue
import ir.smmh.tree.NodedSpecificTree
import java.util.*


class NodedSpecificTreeImpl<DataType>(override val mut: Mut = Mut()) :
    NodedSpecificTree.Mutable<DataType, NodedSpecificTreeImpl<DataType>.Node, NodedSpecificTreeImpl<DataType>> {
    override var rootNode: Node? = null

    private val nodeContainer: CanContainValue<Node> = object : CanContainValue<Node> {
        override fun containsValue(toCheck: NodedSpecificTreeImpl<DataType>.Node) =
            this@NodedSpecificTreeImpl.contains(rootNode, toCheck)

        override fun isEmpty() = this@NodedSpecificTreeImpl.isEmpty()
        override val size: Int get() = this@NodedSpecificTreeImpl.size
    }

    override fun containsValue(toCheck: DataType) = contains(rootNode, toCheck)

    private fun contains(root: Node?, data: DataType?): Boolean {
        if (root == null) return false
        if (root.data == data) return true
        for (child in root.children) {
            if (contains(child, data)) return true
        }
        return false
    }

    private fun contains(root: Node?, node: Node): Boolean {
        if (root == null) return false
        if (root == node) return true
        for (child in root.children) {
            if (contains(child, node)) return true
        }
        return false
    }

    override fun isEmpty(): Boolean {
        return rootNode == null
    }

    override fun nodes(): CanContainValue<Node> {
        return nodeContainer
    }

    override fun getRootData(): DataType? {
        return rootNode?.data
    }

    override fun setRootData(data: DataType) {
        rootNode = Node(data, null)
    }

    override fun specificThis(): NodedSpecificTreeImpl<DataType> = this

    override fun toString() = rootNode?.nodeToString() ?: "{empty}"

    inner class Node(override var data: DataType, parent: Node?) :
        NodedSpecificTree.Mutable.Node<DataType, Node, NodedSpecificTreeImpl<DataType>> {
        // TODO Tree.VariableDegree
        private val _children: Sequential.Mutable.VariableSize<Node?> = SequentialImpl()
        override var parent: Node? = parent
        override fun toString(): String {
            return "<$data>" + if (_children.isEmpty()) "" else _children.toString()
        }

        internal fun nodeToString(): String {
            var childrenString = ""
            if (!_children.isEmpty()) {
                val joiner = StringJoiner(", ", ":(", ")")
                for (child in _children) {
                    joiner.add(child?.nodeToString() ?: "-")
                }
                childrenString = joiner.toString()
            }
            return (data?.toString() ?: "~") + childrenString
        }

        private fun makeNode(data: DataType): Node {
            return Node(data, this)
        }

        override fun asTree(): NodedSpecificTreeImpl<DataType> {
            val subtree = NodedSpecificTreeImpl<DataType>()
            subtree.rootNode = this
            // TODO handle mutation and/or viewing
            return subtree
        }

        override val children: Sequential<Node?> = _children

        override val indexInParent: Int
            get() {
                return this.parent?._children?.findFirst(this) ?: -1
            }

        override val tree: NodedSpecificTreeImpl<DataType>
            get() {
                return this@NodedSpecificTreeImpl
            }

        override fun specificThis(): Node {
            return this
        }
    }
}