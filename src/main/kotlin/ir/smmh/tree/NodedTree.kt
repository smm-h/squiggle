@file:Suppress("unused")

package ir.smmh.tree

import ir.smmh.nile.*
import ir.smmh.nile.Sequential.AbstractSequential
import ir.smmh.nile.verbs.CanAppendTo
import ir.smmh.nile.verbs.CanClone
import ir.smmh.nile.verbs.CanContainValue
import ir.smmh.tree.NodedTree.Node
import ir.smmh.tree.NodedTree.Traversal.*
import ir.smmh.tree.NodedTree.Traversal.Binary.*
import ir.smmh.tree.impl.NodedBinaryTreeImpl
import ir.smmh.tree.impl.NodedTreeImpl
import ir.smmh.tree.impl.TraversedImpl
import ir.smmh.util.FunctionalUtil.and
import java.util.function.Function
import java.util.function.Predicate


/**
 * A `NodedTree` is a [SpecificTree] and a wrapper over a single [Node] called
 * its "root", to which it delegates most methods required by [Tree].
 */

interface NodedTree<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> :
    SpecificTree<DataType, TreeType>, CanClone<TreeType> {
    fun nodes(): CanContainValue<NodeType>
    fun getRootNode(): NodeType?

    /**
     * @param a a node in this tree
     * @param b and another node in this tree
     * @return the nearest node to them that is an ancestor of both
     */
    fun lowestCommonAncestor(a: NodeType, b: NodeType): NodeType {
        if (a === b) return a
        if (a.getParent() === b) return b
        if (b.getParent() === a) return a
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
        val nodes: Sequential.Mutable.VariableSize<NodeType> = SequentialImpl()
        getRootNode()?.fillDescendantsAtExactDepth(nodes, 0, level)
        return nodes
    }

    fun findByData(data: DataType): Sequential<NodeType?> {
        return filter { node: NodeType -> node.getData() == data }
    }

    /**
     * An out-of-place filter done on all the nodes of this tree.
     *
     * @param condition the condition to test the nodes with
     * @return a sequence of nodes that passed the test
     */
    fun filter(condition: (NodeType) -> Boolean): Sequential<NodeType?> {
        return traverse(object : Conditional<DataType, NodeType, TreeType> {
            override fun getCondition() = condition
        }).nodes
    }

    /**
     * An out-of-place prune done on all the nodes of this tree.
     *
     * @param condition the condition to test the nodes with
     * @return a sequence of nodes that passed the test
     */
    fun pruneOutOfPlace(condition: (NodeType) -> Boolean, leafOnly: Boolean): NodedTreeImpl<DataType>? {
        val finalCondition = if (leafOnly) ({ it: NodeType -> it.isLeaf() } and condition) else condition
        val otherTree = NodedTreeImpl<DataType>()
        otherTree.setRootNode(getRootNode()?.pruneOutOfPlace(otherTree, null, finalCondition))
        return otherTree
    }

    fun <OtherDataType> applyOutOfPlace(toApply: (DataType) -> OtherDataType): NodedTreeImpl<OtherDataType> {
        val otherTree = NodedTreeImpl<OtherDataType>()
        otherTree.setRootNode(getRootNode()?.applyOutOfPlace(otherTree, null, toApply))
        return otherTree
    }

    @Suppress("UNCHECKED_CAST")
    override fun clone(deepIfPossible: Boolean): TreeType {
        val otherTree = NodedTreeImpl<DataType>()
        otherTree.setRootNode(getRootNode()?.clone(otherTree, null, deepIfPossible))
        return otherTree as TreeType
    }

    fun toBinary(): NodedBinaryTreeImpl<DataType> {
        val otherTree = NodedBinaryTreeImpl<DataType>()
        val r = getRootNode()
        if (r != null) otherTree.setRootNode(toBinary(r, otherTree, null))
        return otherTree
    }

    fun toBinary(
        node: NodeType,
        otherTree: NodedBinaryTreeImpl<DataType>,
        parent: NodedBinaryTreeImpl<DataType>.Node?
    ): NodedBinaryTreeImpl<DataType>.Node {
        var prev: NodedBinaryTreeImpl<DataType>.Node? = null
        var curr: NodedBinaryTreeImpl<DataType>.Node?
        val otherChild: NodedBinaryTreeImpl<DataType>.Node = otherTree.Node(node.getData(), parent)
        var leftMost = true
        for (child in node.getChildren()) {
            curr = if (child == null) null else toBinary(child, otherTree, otherChild)
            if (leftMost) {
                leftMost = false
                otherChild.setLeftChild(curr)
            } else {
                curr?.setRightChild(prev)
            }
            prev = curr
        }
        return otherChild
    }

    fun traverse(type: Traversal<DataType, NodeType, TreeType>): Traversed<DataType, NodeType, TreeType> {
        val r = getRootNode()
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

    override fun getDegree(): Int {
        return getRootNode()?.getDegree() ?: 0
    }

    override fun getHeight(): Int {
        return getRootNode()?.getHeight() ?: -1
    }

    override val size: Int
        get() = getRootNode()?.getCount() ?: 0

    override fun getBreadth() = getRootNode()?.getLeafCount() ?: 0

    override fun getImmediateSubtrees(): Sequential<TreeType> =
        getRootNode()?.getImmediateSubtrees() ?: Sequential.empty()

    interface Mutable<DataType, NodeType : Mutable.Node<DataType, NodeType, TreeType>, TreeType : Mutable<DataType, NodeType, TreeType>> :
        NodedTree<DataType, NodeType, TreeType>, SpecificTree.Mutable<DataType, TreeType> {
        fun replaceData(toReplace: Function<in DataType, out DataType>) {
            mut.preMutate()
            for (node in traverseBreadthFirst().nodes) {
                node?.replaceData(toReplace)
            }
            mut.mutate()
        }

        fun mutateData(toApply: (DataType) -> Unit) {
            mut.preMutate()
            for (node in traverseBreadthFirst().nodes) {
                node?.mutateData(toApply)
            }
            mut.mutate()
        }

        override fun clear() {
            setRootNode(null)
        }

        fun setRootNode(node: NodeType?)
        interface Node<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : Mutable<DataType, NodeType, TreeType>> :
            NodedTree.Node<DataType, NodeType, TreeType> {
            override fun getChildren(): Sequential<NodeType?>
            fun replaceData(toReplace: Function<in DataType, out DataType>) {
                setData(toReplace.apply(getData()))
            }

            fun mutateData(toApply: (DataType) -> Unit) {
                toApply(getData())
            }
        }
    }

    interface Node<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> :
        RecursivelySpecific<NodeType> {
        /**
         * @return parent and children
         */
        fun getNeighbors(): Sequential<NodeType> {
            val neighbors: Sequential.Mutable.VariableSize<NodeType> = SequentialImpl()
            val parent = getParent()
            if (parent != null) neighbors.append(parent)
            for (child in getChildren()) if (child != null) neighbors.append(child)
            return neighbors
        }

        fun getDescendants(): Sequential<NodeType> {
            val descendants: Sequential.Mutable.VariableSize<NodeType> = SequentialImpl()
            for (child in getChildren()) {
                fillDescendants(descendants)
            }
            return descendants
        }

        fun getDescendantsAndSelf(): Sequential<NodeType> {
            val descendants: Sequential.Mutable.VariableSize<NodeType> = SequentialImpl()
            fillDescendants(descendants)
            return descendants
        }

        fun fillDescendants(canAppendTo: CanAppendTo<NodeType>) {
            canAppendTo.append(specificThis())
            for (child in getChildren()) {
                child!!.fillDescendants(canAppendTo)
            }
        }

        fun getAncestors(): Sequential<NodeType?> {
            val ancestors: Sequential.Mutable.VariableSize<NodeType?> = SequentialImpl()
            var r = getParent()
            while (r != null) {
                ancestors.append(r)
                r = r.getParent()
            }
            return ancestors
        }

        fun getAncestorsAndSelf(): Sequential<NodeType> {
            val ancestors: Sequential.Mutable.VariableSize<NodeType> = SequentialImpl()
            var r: NodeType? = specificThis()
            while (r != null) {
                ancestors.append(r)
                r = r.getParent()
            }
            return ancestors
        }

        fun fillDescendantsAtExactDepth(canAppendTo: CanAppendTo<NodeType>, depth: Int, exactDepth: Int) {
            val nextDepth = depth + 1
            if (nextDepth == exactDepth) {
                canAppendTo.append(specificThis())
            } else {
                for (child in getChildren()) {
                    child!!.fillDescendantsAtExactDepth(canAppendTo, nextDepth, exactDepth)
                }
            }
        }

        fun pruneOutOfPlace(
            otherTree: NodedTreeImpl<DataType>,
            parent: NodedTreeImpl<DataType>.Node?,
            toTest: (NodeType) -> Boolean
        ): NodedTreeImpl<DataType>.Node? {
            val otherChild: NodedTreeImpl<DataType>.Node = otherTree.Node(getData(), parent)
            for (child in getChildren()) {
                if (child != null) {
                    if (toTest(child)) {
                        (otherChild.getChildren() as Sequential.Mutable.VariableSize).append(
                            child.pruneOutOfPlace(
                                otherTree,
                                otherChild,
                                toTest
                            )
                        )
                    }
                }
            }
            return otherChild
        }

        fun <OtherDataType> applyOutOfPlace(
            otherTree: NodedTreeImpl<OtherDataType>,
            parent: NodedTreeImpl<OtherDataType>.Node?,
            toApply: (DataType) -> OtherDataType
        ): NodedTreeImpl<OtherDataType>.Node? {
            val otherChild: NodedTreeImpl<OtherDataType>.Node = otherTree.Node(toApply(getData()), parent)
            for (child in getChildren()) {
                (otherChild.getChildren() as Sequential.Mutable.VariableSize).append(
                    child?.applyOutOfPlace(
                        otherTree,
                        otherChild,
                        toApply
                    )
                )
            }
            return otherChild
        }

        @Suppress("UNCHECKED_CAST")
        fun clone(
            otherTree: NodedTreeImpl<DataType>,
            parent: NodedTreeImpl<DataType>.Node?,
            deep: Boolean
        ): NodedTreeImpl<DataType>.Node? {
            var data = getData()
            if (deep && data is CanClone<*>) {
                data = (data as CanClone<DataType>).clone(true)
            }
            val otherChild: NodedTreeImpl<DataType>.Node = otherTree.Node(data, parent)
            for (child in getChildren()) {
                (otherChild.getChildren() as Sequential.Mutable.VariableSize).append(
                    child!!.clone(
                        otherTree,
                        otherChild,
                        deep
                    )
                )
            }
            return otherChild
        }

        fun isLeaf(): Boolean {
            return getChildren().isEmpty()
        }

        fun getSiblings(): Sequential<NodeType?> {
            val parent = getParent()
            return if (parent == null) Sequential.empty()
            else SequentialImpl(parent.getChildren()).apply { removeIndexFrom(getIndexInParent()) }
        }

        fun asTree(): TreeType
        fun getImmediateSubtrees(): Sequential<TreeType> {
            val subtrees: Sequential.Mutable.VariableSize<TreeType> = SequentialImpl()
            for (child in getChildren()) {
                subtrees.append(child!!.asTree())
            }
            return subtrees
        }

        fun getChildren(): Sequential<NodeType?>
        fun getChildrenData(): Sequential<DataType?> {
            return object : AbstractSequential<DataType?>() {
                override fun getAtIndex(index: Int): DataType? {
                    return getChildren().getAtIndex(index)!!.getData()
                }

                override val size: Int
                    get() = getChildren().size
            }
        }

        fun getIndexInParent(): Int
        fun getData(): DataType
        fun setData(data: DataType)
        fun hasData(): Boolean {
            return getData() != null
        }

        fun getParent(): NodeType?
        fun setParent(parent: NodeType?)
        fun getTree(): TreeType
        fun getDegree(): Int {
            return if (getChildren().isEmpty()) {
                0
            } else {
                var degree = getChildren().size
                for (child in getChildren()) {
                    degree = degree.coerceAtLeast(child?.getDegree() ?: 0)
                }
                degree
            }
        }

        fun getHeight(): Int {
            return if (getChildren().isEmpty()) {
                0
            } else {
                var height = 0
                for (child in getChildren()) {
                    height = height.coerceAtLeast(child?.getHeight() ?: 0)
                    height = height.coerceAtLeast(child?.getHeight() ?: 0)
                }
                height + 1
            }
        }

        fun getCount(): Int {
            return if (getChildren().isEmpty()) {
                1
            } else {
                var count = 0
                for (child in getChildren()) {
                    count += child?.getCount() ?: 0
                }
                count
            }
        }

        fun getLeafCount(): Int {
            return if (getChildren().isEmpty()) {
                1
            } else {
                var leafCount = 0
                for (child in getChildren()) {
                    leafCount += child?.getLeafCount() ?: 0
                }
                leafCount
            }
        }
    }

    interface Binary<DataType, NodeType : Binary.Node<DataType, NodeType, TreeType>, TreeType : Binary<DataType, NodeType, TreeType>> :
        NodedTree<DataType, NodeType, TreeType>, SpecificTree.Binary<DataType, TreeType> {
        override fun getDegree(): Int {
            return super<NodedTree>@Binary.getDegree()
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
            Binary<DataType, NodeType, TreeType>, NodedTree.Mutable<DataType, NodeType, TreeType>,
            SpecificTree.Binary.Mutable<DataType, TreeType> {
            interface Node<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : Mutable<DataType, NodeType, TreeType>> :
                Binary.Node<DataType, NodeType, TreeType>, NodedTree.Mutable.Node<DataType, NodeType, TreeType> {
                fun setLeftChild(leftChild: NodeType?)
                fun setRightChild(rightChild: NodeType?)
            }
        }

        interface Node<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : Binary<DataType, NodeType, TreeType>> :
            NodedTree.Node<DataType, NodeType, TreeType> {
            fun getLeftChild(): NodeType?
            fun getRightChild(): NodeType?
        }
    }

    interface Traversal<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> {
        fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType>
        interface Conditional<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> :
            Traversal<DataType, NodeType, TreeType> {
            override fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType> {
                val seq: Sequential.Mutable.VariableSize<NodeType?> = SequentialImpl()
                fillNodes(root.specificThis(), seq, getCondition())
                return Traversed.of(seq, this)
            }

            fun getCondition(): (NodeType) -> Boolean
            fun fillNodes(node: NodeType, canAppendTo: CanAppendTo<in NodeType>, condition: Predicate<in NodeType>) {
                if (condition.test(node)) {
                    canAppendTo.add(node)
                }
                for (child in node.getChildren()) {
                    if (child != null) {
                        fillNodes(child, canAppendTo, condition)
                    }
                }
            }
        }

        @FunctionalInterface
        interface ByOrder<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> :
            Traversal<DataType, NodeType, TreeType> {
            override fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType> {
                val seq: Sequential.Mutable.VariableSize<NodeType?> = SequentialImpl()
                val order = makeOrder(root.getCount())
                order.enter(root)
                while (true) {
                    val node = order.pollNullable() ?: break
                    seq.append(node)
                    for (child in node.getChildren()) {
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
        interface ByOrderReverseChildren<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> :
            Traversal<DataType, NodeType, TreeType> {
            override fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType> {
                val seq: Sequential.Mutable.VariableSize<NodeType?> = SequentialImpl()
                val order = makeOrder(root.getCount())
                order.enter(root)
                while (true) {
                    val node = order.pollNullable() ?: break
                    seq.append(node)
                    for (child in node.getChildren().inReverse()) {
                        if (child != null) {
                            order.enter(child)
                        }
                    }
                }
                return Traversed.of(seq, this)
            }

            fun makeOrder(capacity: Int): Order<NodeType>
        }

        abstract class Binary<DataType, NodeType : NodedTree.Binary.Node<DataType, NodeType, TreeType>, TreeType : NodedTree.Binary<DataType, NodeType, TreeType>> :
            Traversal<DataType, NodeType, TreeType> {
            protected val seq: Sequential.Mutable.VariableSize<NodeType?> = SequentialImpl()

            // USE INT-KEY MAPS INSTEAD OF SEQUENTIALS

            override fun traverse(root: NodeType): Traversed<DataType, NodeType, TreeType> {
                assert(root.getDegree() <= 2)
                seq.clear()
                fillData(root)
                return Traversed.of(seq, this)
            }

            abstract fun fillData(node: NodeType?)
            class PreOrder<DataType, NodeType : NodedTree.Binary.Node<DataType, NodeType, TreeType>, TreeType : NodedTree.Binary<DataType, NodeType, TreeType>> :
                Binary<DataType, NodeType, TreeType>() {
                override fun fillData(node: NodeType?) {
                    if (node != null) {
                        seq.add(node)
                        fillData(node.getLeftChild())
                        fillData(node.getRightChild())
                    }
                }
            }

            class InOrder<DataType, NodeType : NodedTree.Binary.Node<DataType, NodeType, TreeType>, TreeType : NodedTree.Binary<DataType, NodeType, TreeType>> :
                Binary<DataType, NodeType, TreeType>() {
                override fun fillData(node: NodeType?) {
                    if (node != null) {
                        fillData(node.getLeftChild())
                        seq.add(node)
                        fillData(node.getRightChild())
                    }
                }
            }

            class PostOrder<DataType, NodeType : NodedTree.Binary.Node<DataType, NodeType, TreeType>, TreeType : NodedTree.Binary<DataType, NodeType, TreeType>> :
                Binary<DataType, NodeType, TreeType>() {
                override fun fillData(node: NodeType?) {
                    if (node != null) {
                        fillData(node.getLeftChild())
                        fillData(node.getRightChild())
                        seq.add(node)
                    }
                }
            }
        }

        class LeafOnly<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> :
            Conditional<DataType, NodeType, TreeType> {
            override fun getCondition(): (NodeType) -> Boolean {
                return { it.isLeaf() }
            }
        }

        class NonLeafOnly<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> :
            Conditional<DataType, NodeType, TreeType> {
            override fun getCondition(): (NodeType) -> Boolean {
                return { !it.isLeaf() }
            }
        }

        class HasDataOnly<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> :
            Conditional<DataType, NodeType, TreeType> {
            override fun getCondition(): (NodeType) -> Boolean {
                return { it.hasData() }
            }
        }
    }

    interface Traversed<DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> {
        val nodes: Sequential<NodeType?>
        val data: Sequential<DataType?>
        val type: Traversal<DataType, NodeType, TreeType>

        companion object {
            fun <DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> empty(
                type: Traversal<DataType, NodeType, TreeType>
            ): Traversed<DataType, NodeType, TreeType> {
                return TraversedImpl(Sequential.empty(), type)
            }

            fun <DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> of(
                sequential: Sequential<NodeType?>,
                type: Traversal<DataType, NodeType, TreeType>
            ): Traversed<DataType, NodeType, TreeType> {
                return TraversedImpl(sequential, type)
            }

            fun <DataType, NodeType : Node<DataType, NodeType, TreeType>, TreeType : NodedTree<DataType, NodeType, TreeType>> of(
                map: Map<Int, NodeType>,
                type: Traversal<DataType, NodeType, TreeType>
            ): Traversed<DataType, NodeType, TreeType> {
                return TraversedImpl(Sequential.of(map), type)
            }
        }
    }
}