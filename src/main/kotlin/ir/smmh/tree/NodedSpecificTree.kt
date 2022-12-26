@file:Suppress("unused")

package ir.smmh.tree

import ir.smmh.nile.*
import ir.smmh.nile.Sequential.AbstractSequential
import ir.smmh.nile.verbs.CanAppendTo
import ir.smmh.nile.verbs.CanClone
import ir.smmh.nile.verbs.CanContainValue
import ir.smmh.tree.NodedSpecificTree.Node
import ir.smmh.tree.NodedSpecificTree.Traversal.*
import ir.smmh.tree.NodedSpecificTree.Traversal.Binary.*
import ir.smmh.tree.impl.NodedBinarySpecificTreeImpl
import ir.smmh.tree.impl.NodedSpecificTreeImpl
import ir.smmh.tree.impl.TraversedImpl
import ir.smmh.util.FunctionalUtil.and
import java.util.function.Function
import java.util.function.Predicate


/**
 * A [NodedSpecificTree] is a [SpecificTree] and a wrapper over a single [Node]
 * called its "root", to which it delegates most methods required by [Tree].
 */

interface NodedSpecificTree<DataType, NodeType : NodedSpecificTree.Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> :
    SpecificTree<DataType, TreeType>, CanClone<TreeType> {
    fun nodes(): CanContainValue<NodeType>
    val rootNode: NodeType?

    /**
     * @param a a node in this tree
     * @param b and another node in this tree
     * @return the nearest node to them that is an ancestor of both
     */
    fun lowestCommonAncestor(a: NodeType, b: NodeType): NodeType {
        if (a === b) return a
        if (a.parent === b) return b
        if (b.parent === a) return a
        val aS = (a.getAncestorsAndSelf() as Sequential.Mutable).apply { reverseInplace() }
        val bS = (b.getAncestorsAndSelf() as Sequential.Mutable).apply { reverseInplace() }
        var i = 0
        while (aS.getAtIndex(i) == bS.getAtIndex(i)) i++
        return aS.getAtIndex(i - 1)
    }

    override fun getWidth(level: Int): Int {
        return getNodesAtLevel(level).size
    }

    fun getNodesAtLevel(level: Int): Sequential<NodeType> {
        val nodes: Sequential.Mutable.CanChangeSize<NodeType> = ListSequential()
        rootNode?.fillDescendantsAtExactDepth(nodes, 0, level)
        return nodes
    }

    fun findByData(data: DataType): Sequential<NodeType?> {
        return filter { node: NodeType -> node.data == data }
    }

    /**
     * An out-of-place filter done on all the nodes of this tree.
     *
     * @param condition the condition to test the nodes with
     * @return a sequence of nodes that passed the test
     */
    fun filter(condition: (NodeType) -> Boolean): Sequential<NodeType?> {
        return traverse(object : Conditional<DataType, NodeType, TreeType> {
            override val condition get() = condition
        }).nodes
    }

    /**
     * An out-of-place prune done on all the nodes of this tree.
     *
     * @param condition the condition to test the nodes with
     * @return a sequence of nodes that passed the test
     */
    fun pruneOutOfPlace(condition: (NodeType) -> Boolean, leafOnly: Boolean): NodedSpecificTreeImpl<DataType>? {
        val finalCondition = if (leafOnly) ({ it: NodeType -> it.isLeaf() } and condition) else condition
        val otherTree = NodedSpecificTreeImpl<DataType>()
        otherTree.rootNode = rootNode?.pruneOutOfPlace(otherTree, null, finalCondition)
        return otherTree
    }

    fun <OtherDataType> applyOutOfPlace(toApply: (DataType) -> OtherDataType): NodedSpecificTreeImpl<OtherDataType> {
        val otherTree = NodedSpecificTreeImpl<OtherDataType>()
        otherTree.rootNode = rootNode?.applyOutOfPlace(otherTree, null, toApply)
        return otherTree
    }

    @Suppress("UNCHECKED_CAST")
    override fun clone(deepIfPossible: Boolean): TreeType {
        val otherTree = NodedSpecificTreeImpl<DataType>()
        otherTree.rootNode = rootNode?.clone(otherTree, null, deepIfPossible)
        return otherTree as TreeType
    }

    fun toBinary(): NodedBinarySpecificTreeImpl<DataType> {
        val otherTree = NodedBinarySpecificTreeImpl<DataType>()
        val r = rootNode
        if (r != null) otherTree.rootNode = toBinary(r, otherTree, null)
        return otherTree
    }

    fun toBinary(
        node: NodeType,
        otherTree: NodedBinarySpecificTreeImpl<DataType>,
        parent: NodedBinarySpecificTreeImpl<DataType>.Node?
    ): NodedBinarySpecificTreeImpl<DataType>.Node {
        var prev: NodedBinarySpecificTreeImpl<DataType>.Node? = null
        var curr: NodedBinarySpecificTreeImpl<DataType>.Node?
        val otherChild: NodedBinarySpecificTreeImpl<DataType>.Node = otherTree.Node(node.data, parent)
        var leftMost = true
        for (child in node.children) {
            curr = if (child == null) null else toBinary(child, otherTree, otherChild)
            if (leftMost) {
                leftMost = false
                otherChild.leftChild = curr
            } else {
                curr?.rightChild = prev
            }
            prev = curr
        }
        return otherChild
    }

    fun traverse(type: Traversal<DataType, NodeType, TreeType>): Traversed<DataType, NodeType, TreeType> {
        val r = rootNode
        return if (r == null) Traversed.empty(type) else type.traverse(r)
    }

    fun traverseLeafOnly(): Traversed<DataType, NodeType, TreeType> {
        return traverse(LeafOnly())
    }

    fun getLeafNodes(): Sequential<NodeType?> {
        return traverseLeafOnly().nodes
    }

    override fun getLeafData(): Sequential<DataType?> {
        return traverseLeafOnly().data
    }

    override fun getBreadthFirstData(): Sequential<DataType?> {
        return traverseBreadthFirst().data
    }

    override fun getDepthFirstData(): Sequential<DataType?> {
        return traverseDepthFirst().data
    }

    fun traverseBreadthFirst(): Traversed<DataType, NodeType, TreeType> {
        return traverse(object : ByOrder<DataType, NodeType, TreeType> {
            override fun makeOrder(capacity: Int) = ArrayQueue<NodeType>(capacity)
        })
    }

    fun traverseDepthFirst(): Traversed<DataType, NodeType, TreeType> {
        return traverse(object : ByOrderReverseChildren<DataType, NodeType, TreeType> {
            override fun makeOrder(capacity: Int) = ArrayStack<NodeType>(capacity)
        })
    }

    override val degree: Int
        get() {
            return rootNode?.degree ?: 0
        }

    override val height: Int
        get() {
            return rootNode?.height ?: -1
        }

    override val size: Int
        get() = rootNode?.count ?: 0

    override fun getBreadth() = rootNode?.getLeafCount() ?: 0

    override fun getImmediateSubtrees(): Sequential<TreeType> =
        rootNode?.getImmediateSubtrees() ?: Sequential.empty()

    interface Mutable<DataType, NodeType : Mutable.Node<DataType, NodeType, TreeType>, TreeType : Mutable<DataType, NodeType, TreeType>> :
        NodedSpecificTree<DataType, NodeType, TreeType>, SpecificTree.Mutable<DataType, TreeType> {
        fun replaceData(toReplace: Function<in DataType, out DataType>) {
            changesToValues.beforeChange()
            for (node in traverseBreadthFirst().nodes) {
                node?.replaceData(toReplace)
            }
            changesToValues.afterChange()
        }

        fun mutateData(toApply: (DataType) -> Unit) {
            changesToValues.beforeChange()
            for (node in traverseBreadthFirst().nodes) {
                node?.mutateData(toApply)
            }
            changesToValues.afterChange()
        }

        override fun clear() {
            changesToSize.beforeChange()
            rootNode = null
            changesToSize.afterChange()
        }

        override var rootNode: NodeType?

        interface Node<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : Mutable<DataType, NodeType, TreeType>> :
            NodedSpecificTree.Node<DataType, NodeType, TreeType> {
            override val children: Sequential<NodeType?>
            fun replaceData(toReplace: Function<in DataType, out DataType>) {
                data = toReplace.apply(data)
            }

            fun mutateData(toApply: (DataType) -> Unit) {
                toApply(data)
            }
        }
    }

    interface Node<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> :
        RecursivelySpecific<NodeType> {
        /**
         * @return parent and children
         */
        fun getNeighbors(): Sequential<NodeType> {
            val neighbors: Sequential.Mutable.CanChangeSize<NodeType> = ListSequential()
            val parent = parent
            if (parent != null) neighbors.append(parent)
            for (child in children) if (child != null) neighbors.append(child)
            return neighbors
        }

        fun getDescendants(): Sequential<NodeType> {
            val descendants: Sequential.Mutable.CanChangeSize<NodeType> = ListSequential()
            for (child in children) {
                fillDescendants(descendants)
            }
            return descendants
        }

        fun getDescendantsAndSelf(): Sequential<NodeType> {
            val descendants: Sequential.Mutable.CanChangeSize<NodeType> = ListSequential()
            fillDescendants(descendants)
            return descendants
        }

        fun fillDescendants(canAppendTo: CanAppendTo<NodeType>) {
            canAppendTo.append(specificThis())
            for (child in children) {
                child!!.fillDescendants(canAppendTo)
            }
        }

        fun getAncestors(): Sequential<NodeType?> {
            val ancestors: Sequential.Mutable.CanChangeSize<NodeType?> = ListSequential()
            var r = parent
            while (r != null) {
                ancestors.append(r)
                r = r.parent
            }
            return ancestors
        }

        fun getAncestorsAndSelf(): Sequential<NodeType> {
            val ancestors: Sequential.Mutable.CanChangeSize<NodeType> = ListSequential()
            var r: NodeType? = specificThis()
            while (r != null) {
                ancestors.append(r)
                r = r.parent
            }
            return ancestors
        }

        fun fillDescendantsAtExactDepth(canAppendTo: CanAppendTo<NodeType>, depth: Int, exactDepth: Int) {
            val nextDepth = depth + 1
            if (nextDepth == exactDepth) {
                canAppendTo.append(specificThis())
            } else {
                for (child in children) {
                    child!!.fillDescendantsAtExactDepth(canAppendTo, nextDepth, exactDepth)
                }
            }
        }

        fun pruneOutOfPlace(
            otherTree: NodedSpecificTreeImpl<DataType>,
            parent: NodedSpecificTreeImpl<DataType>.Node?,
            toTest: (NodeType) -> Boolean
        ): NodedSpecificTreeImpl<DataType>.Node? {
            val otherChild: NodedSpecificTreeImpl<DataType>.Node = otherTree.Node(data, parent)
            for (child in children) {
                if (child != null && toTest(child)) {
                    (otherChild.children).append(child.pruneOutOfPlace(otherTree, otherChild, toTest))
                }
            }
            return otherChild
        }

        fun <OtherDataType> applyOutOfPlace(
            otherTree: NodedSpecificTreeImpl<OtherDataType>,
            parent: NodedSpecificTreeImpl<OtherDataType>.Node?,
            toApply: (DataType) -> OtherDataType
        ): NodedSpecificTreeImpl<OtherDataType>.Node? {
            val otherChild: NodedSpecificTreeImpl<OtherDataType>.Node = otherTree.Node(toApply(data), parent)
            for (child in children) {
                (otherChild.children).append(child?.applyOutOfPlace(otherTree, otherChild, toApply))
            }
            return otherChild
        }

        @Suppress("UNCHECKED_CAST")
        fun clone(
            otherTree: NodedSpecificTreeImpl<DataType>,
            parent: NodedSpecificTreeImpl<DataType>.Node?,
            deep: Boolean
        ): NodedSpecificTreeImpl<DataType>.Node? {
            var data = data
            if (deep && data is CanClone<*>) {
                data = (data as CanClone<DataType>).clone(true)
            }
            val otherChild: NodedSpecificTreeImpl<DataType>.Node = otherTree.Node(data, parent)
            for (child in children) {
                (otherChild.children).append(child!!.clone(otherTree, otherChild, deep))
            }
            return otherChild
        }

        fun isLeaf(): Boolean {
            return children.isEmpty()
        }

        fun getSiblings(): Sequential<NodeType?> {
            val parent = parent
            return if (parent == null) Sequential.empty()
            else ListSequential(parent.children).apply { removeIndexFrom(indexInParent) }
        }

        fun asTree(): TreeType
        fun getImmediateSubtrees(): Sequential<TreeType> {
            val subtrees: Sequential.Mutable.CanChangeSize<TreeType> = ListSequential()
            for (child in children) {
                subtrees.append(child!!.asTree())
            }
            return subtrees
        }

        val children: Sequential<NodeType?>
        fun getChildrenData(): Sequential<DataType?> {
            return object : AbstractSequential<DataType?>() {
                override fun getAtIndex(index: Int): DataType? {
                    return children.getAtIndex(index)!!.data
                }

                override val size: Int get() = children.size
            }
        }

        val indexInParent: Int
        var data: DataType
        fun hasData(): Boolean {
            return data != null
        }

        var parent: NodeType?
        val tree: TreeType
        val degree: Int
            get() {
                return if (children.isEmpty()) 0 else {
                    var degree = children.size
                    for (child in children) {
                        degree = degree.coerceAtLeast(child?.degree ?: 0)
                    }
                    degree
                }
            }

        val height: Int
            get() = if (children.isEmpty()) 0 else {
                var height = 0
                for (child in children) {
                    height = height.coerceAtLeast(child?.height ?: 0)
                    height = height.coerceAtLeast(child?.height ?: 0)
                }
                height + 1
            }

        val count: Int
            get() = if (children.isEmpty()) 1 else {
                var count = 0
                for (child in children) {
                    count += child?.count ?: 0
                }
                count
            }

        fun getLeafCount(): Int = if (children.isEmpty()) 1 else {
            var leafCount = 0
            for (child in children) {
                leafCount += child?.getLeafCount() ?: 0
            }
            leafCount
        }
    }

    interface Binary<DataType, NodeType : Binary.Node<DataType, NodeType, TreeType>, TreeType : Binary<DataType, NodeType, TreeType>> :
        NodedSpecificTree<DataType, NodeType, TreeType>, SpecificTree.Binary<DataType, TreeType> {
        override val degree: Int
            get() {
                return super<NodedSpecificTree>@Binary.degree
            }

        fun traversePreOrder(): Traversed<DataType, NodeType, TreeType> = traverse(PreOrder())
        fun traverseInOrder(): Traversed<DataType, NodeType, TreeType> = traverse(InOrder())
        fun traversePostOrder(): Traversed<DataType, NodeType, TreeType> = traverse(PostOrder())

        override fun traverseDataPreOrder(): Sequential<DataType?> {
            return traversePreOrder().data
        }

        override fun traverseDataInOrder(): Sequential<DataType?> {
            return traverseInOrder().data
        }

        override fun traverseDataPostOrder(): Sequential<DataType?> {
            return traversePostOrder().data
        }

        interface Mutable<DataType, NodeType : Mutable.Node<DataType, NodeType, TreeType>, TreeType : Mutable<DataType, NodeType, TreeType>> :
            Binary<DataType, NodeType, TreeType>, NodedSpecificTree.Mutable<DataType, NodeType, TreeType>,
            SpecificTree.Binary.Mutable<DataType, TreeType> {
            interface Node<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : Mutable<DataType, NodeType, TreeType>> :
                Binary.Node<DataType, NodeType, TreeType>,
                NodedSpecificTree.Mutable.Node<DataType, NodeType, TreeType> {
                override var leftChild: NodeType?
                override var rightChild: NodeType?
            }
        }

        interface Node<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : Binary<DataType, NodeType, TreeType>> :
            NodedSpecificTree.Node<DataType, NodeType, TreeType> {
            val leftChild: NodeType?
            val rightChild: NodeType?
        }
    }

    interface Traversal<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> {
        fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType>
        interface Conditional<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> :
            Traversal<DataType, NodeType, TreeType> {
            override fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType> {
                val seq: Sequential.Mutable.CanChangeSize<NodeType?> = ListSequential()
                fillNodes(root.specificThis(), seq, condition)
                return Traversed.of(seq, this)
            }

            val condition: (NodeType) -> Boolean
            fun fillNodes(node: NodeType, canAppendTo: CanAppendTo<in NodeType>, condition: Predicate<in NodeType>) {
                if (condition.test(node)) {
                    canAppendTo.add(node)
                }
                for (child in node.children) {
                    if (child != null) {
                        fillNodes(child, canAppendTo, condition)
                    }
                }
            }
        }

        @FunctionalInterface
        interface ByOrder<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> :
            Traversal<DataType, NodeType, TreeType> {
            override fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType> {
                val seq: Sequential.Mutable.CanChangeSize<NodeType?> = ListSequential()
                val order = makeOrder(root.count)
                order.enter(root)
                while (true) {
                    val node = order.pollNullable() ?: break
                    seq.append(node)
                    for (child in node.children) {
                        if (child != null) {
                            order.enter(child)
                        }
                    }
                }
                return Traversed.of(seq, this)
            }

            fun makeOrder(capacity: Int): Order<NodeType>
        }

        @FunctionalInterface
        interface ByOrderReverseChildren<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> :
            Traversal<DataType, NodeType, TreeType> {
            override fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType> {
                val seq: Sequential.Mutable.CanChangeSize<NodeType?> = ListSequential()
                val order = makeOrder(root.count)
                order.enter(root)
                while (true) {
                    val node = order.pollNullable() ?: break
                    seq.append(node)
                    for (child in node.children.inReverse()) {
                        if (child != null) {
                            order.enter(child)
                        }
                    }
                }
                return Traversed.of(seq, this)
            }

            fun makeOrder(capacity: Int): Order<NodeType>
        }

        abstract class Binary<DataType, NodeType : NodedSpecificTree.Binary.Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree.Binary<DataType, NodeType, TreeType>> :
            Traversal<DataType, NodeType, TreeType> {
            protected val seq: Sequential.Mutable.CanChangeSize<NodeType?> = ListSequential()

            // USE INT-KEY MAPS INSTEAD OF SEQUENTIALS

            override fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType> {
                assert(root.degree <= 2)
                seq.clear()
                fillData(root)
                return Traversed.of(seq, this)
            }

            abstract fun fillData(node: NodeType?)
            class PreOrder<DataType, NodeType : NodedSpecificTree.Binary.Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree.Binary<DataType, NodeType, TreeType>> :
                Binary<DataType, NodeType, TreeType>() {
                override fun fillData(node: NodeType?) {
                    if (node != null) {
                        seq.add(node)
                        fillData(node.leftChild)
                        fillData(node.rightChild)
                    }
                }
            }

            class InOrder<DataType, NodeType : NodedSpecificTree.Binary.Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree.Binary<DataType, NodeType, TreeType>> :
                Binary<DataType, NodeType, TreeType>() {
                override fun fillData(node: NodeType?) {
                    if (node != null) {
                        fillData(node.leftChild)
                        seq.add(node)
                        fillData(node.rightChild)
                    }
                }
            }

            class PostOrder<DataType, NodeType : NodedSpecificTree.Binary.Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree.Binary<DataType, NodeType, TreeType>> :
                Binary<DataType, NodeType, TreeType>() {
                override fun fillData(node: NodeType?) {
                    if (node != null) {
                        fillData(node.leftChild)
                        fillData(node.rightChild)
                        seq.add(node)
                    }
                }
            }
        }

        class LeafOnly<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> :
            Conditional<DataType, NodeType, TreeType> {
            override val condition: (NodeType) -> Boolean
                get() {
                    return { it.isLeaf() }
                }
        }

        class NonLeafOnly<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> :
            Conditional<DataType, NodeType, TreeType> {
            override val condition: (NodeType) -> Boolean
                get() {
                    return { !it.isLeaf() }
                }
        }

        class HasDataOnly<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> :
            Conditional<DataType, NodeType, TreeType> {
            override val condition: (NodeType) -> Boolean
                get() {
                    return { it.hasData() }
                }
        }
    }

    interface Traversed<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> {
        val nodes: Sequential<NodeType?>
        val data: Sequential<DataType?>
        val type: Traversal<DataType, NodeType, TreeType>

        companion object {
            fun <DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> empty(
                type: Traversal<DataType, NodeType, TreeType>
            ): Traversed<DataType, NodeType, TreeType> {
                return TraversedImpl(Sequential.empty(), type)
            }

            fun <DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> of(
                sequential: Sequential<NodeType?>,
                type: Traversal<DataType, NodeType, TreeType>
            ): Traversed<DataType, NodeType, TreeType> {
                return TraversedImpl(sequential, type)
            }

            fun <DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedSpecificTree<DataType, NodeType, TreeType>> of(
                map: Map<Int, NodeType>,
                type: Traversal<DataType, NodeType, TreeType>
            ): Traversed<DataType, NodeType, TreeType> {
                return TraversedImpl(Sequential.of(map), type)
            }
        }
    }
}