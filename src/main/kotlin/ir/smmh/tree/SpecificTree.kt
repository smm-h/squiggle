package ir.smmh.tree

import ir.smmh.nile.RecursivelySpecific
import ir.smmh.nile.Sequential

/**
 * A `SpecificTree` is a [Tree] whose subtrees are all of the same type as itself.
 *
 * @param <DataType> Data type
 * @param <TreeType> Specific tree type
 */
interface SpecificTree<DataType, TreeType : SpecificTree<DataType, TreeType>> : Tree<DataType>,
    RecursivelySpecific<TreeType> {
    override fun getImmediateSubtrees(): Sequential<TreeType>
    interface Mutable<DataType, TreeType : Mutable<DataType, TreeType>> : SpecificTree<DataType, TreeType>,
        Tree.Mutable<DataType>

    interface Binary<DataType, TreeType : Binary<DataType, TreeType>> : SpecificTree<DataType, TreeType>,
        Tree.Binary<DataType> {
        interface Mutable<DataType, TreeType : Mutable<DataType, TreeType>> : Binary<DataType, TreeType>,
            SpecificTree.Mutable<DataType, TreeType>, Tree.Binary.Mutable<DataType>
    }
}