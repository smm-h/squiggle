package ir.smmh.blockchain

import ir.smmh.util.SecurityUtil.hash

object BlockchainTest : Blockchain<BlockchainTest.Data, BlockchainTest.Block> {
    data class Data(val data: String) : Blockchain.BlockData {
        override fun mergeHash(previousHash: String) = (previousHash + data).hash("sha-256")
        override fun encode() = data
    }

    class Block(private val previousBlock: Block?, override val data: Data) : Blockchain.Block<Data> {
        var nextBlock: Block? = null
        override val previousHash: String = previousBlock?.hash ?: ""
        override val hash: String = data.mergeHash(previousHash)

        init {
            previousBlock?.nextBlock = this
        }
    }

    private var last: Block? = null
    private var first: Block? = null

    override val lastBlock: Block? = last

    override fun add(data: Data): Block {
        val block = Block(last, data)
        last = block
        return block
    }

    override fun verify(): Boolean {
        var prev: Block?
        var curr: Block = first!!
        while (true) {
            val next = curr.nextBlock ?: return true
            prev = curr
            curr = next
            if (curr.hash != curr.data.mergeHash(prev.hash) || curr.previousHash != prev.hash) break
        }
        return false
    }
}

// TODO test this blockchain