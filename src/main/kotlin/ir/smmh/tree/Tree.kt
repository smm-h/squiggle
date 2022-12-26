package ir.smmh.tree

import ir.smmh.nile.Sequential
import ir.smmh.nile.verbs.CanChangeValues
import ir.smmh.nile.verbs.CanClear
import ir.smmh.nile.verbs.CanContainValue
import ir.smmh.tree.impl.InOrderConstructor
import ir.smmh.tree.impl.PostOrderConstructor
import ir.smmh.tree.impl.PreOrderConstructor

interface Tree<DataType> : CanContainValue<DataType> { // , CanSerialize
    fun getImmediateSubtrees(): Sequential<out Tree<DataType>>
    val degree: Int
    val height: Int

    /**
     * @return the number of leaves
     */
    fun getBreadth(): Int

    /**
     * Returns the width of the tree in a given level which is the number
     * of all the nodes that have the same distance from root.
     *
     * @param level the exact distance between root and nodes
     * @return the width of the tree in a given level
     */
    fun getWidth(level: Int): Int
    fun getLeafData(): Sequential<DataType?>
    fun getBreadthFirstData(): Sequential<DataType?>
    fun getDepthFirstData(): Sequential<DataType?>
    val rootData: DataType?

    interface Mutable<DataType> : Tree<DataType>, CanClear, CanChangeValues {
        override var rootData: DataType?
    }

    interface Binary<DataType> : Tree<DataType> {
        fun traverseDataPreOrder(): Sequential<DataType?>
        fun traverseDataInOrder(): Sequential<DataType?>
        fun traverseDataPostOrder(): Sequential<DataType?>
        override val degree: Int
            get() {
                return 2
            }

        interface Mutable<DataType> : Binary<DataType>, Tree.Mutable<DataType>

        /**
         * An order constructor is a binary tree constructor that uses two data traversals
         * out of the three available for binary trees (pre, in and post order) to construct
         * the binary tree itself and get the third data traversal.
         */
        interface OrderConstructor<DataType> {
            fun getFirstSource(): Sequential<DataType>
            fun getSecondSource(): Sequential<DataType>
            fun getTarget(): Sequential<DataType?>
            fun getTree(): Binary<DataType>

            companion object {
                @kotlin.jvm.JvmStatic
                fun <DataType> targetPreOrder(
                    inOrder: Sequential<DataType>,
                    postOrder: Sequential<DataType>
                ): OrderConstructor<DataType> {
                    return PreOrderConstructor(inOrder, postOrder)
                }

                @kotlin.jvm.JvmStatic
                fun <DataType> targetInOrder(
                    preOrder: Sequential<DataType>,
                    postOrder: Sequential<DataType>
                ): OrderConstructor<DataType> {
                    return InOrderConstructor(preOrder, postOrder)
                }

                @kotlin.jvm.JvmStatic
                fun <DataType> targetPostOrder(
                    preOrder: Sequential<DataType>,
                    inOrder: Sequential<DataType>
                ): OrderConstructor<DataType> {
                    return PostOrderConstructor(preOrder, inOrder)
                }
            }
        }
    }
}