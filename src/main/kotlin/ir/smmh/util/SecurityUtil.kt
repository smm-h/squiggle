package ir.smmh.util

import java.security.MessageDigest

object SecurityUtil {
    private const val HEX_CHARS = "0123456789abcdef"

    fun String.toHexString() = toByteArray(Charsets.UTF_8).toHexString()

    fun ByteArray.toHexString(): String {
        val builder = StringBuilder(size * 2)

        forEach {
            val i = it.toInt()
            builder.append(HEX_CHARS[i shr 4 and 0x0f])
            builder.append(HEX_CHARS[i and 0x0f])
        }

        return builder.toString()
    }

    fun String.hash(algorithm: String): String {
        return MessageDigest.getInstance(algorithm).digest(toByteArray(Charsets.UTF_8)).toHexString()
    }

}