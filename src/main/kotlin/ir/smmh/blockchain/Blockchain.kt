package ir.smmh.blockchain

interface Blockchain<
        BLOCK_DATA : Blockchain.BlockData,
        BLOCK : Blockchain.Block<BLOCK_DATA>
        > {
    val lastBlock: BLOCK?
    fun add(data: BLOCK_DATA): BLOCK
    fun verify(): Boolean

    interface Block<BLOCK_DATA : BlockData> {
        val data: BLOCK_DATA
        val hash: String
        val previousHash: String
    }

    interface BlockData {
        fun mergeHash(previousHash: String): String
        fun encode(): String
    }
}