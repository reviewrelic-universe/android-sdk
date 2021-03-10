package com.review.relic.utils

import java.nio.charset.Charset

object Hex {
    private val DIGITS_LOWER: CharArray = charArrayOf(
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f'
    )
    private val DIGITS_UPPER: CharArray = charArrayOf(
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'A',
        'B',
        'C',
        'D',
        'E',
        'F'
    )

    fun encodeHexString(data: ByteArray): String {
        return String(encodeHex(data))
    }

    @JvmOverloads
    fun encodeHex(data: ByteArray, toLowerCase: Boolean = true): CharArray {
        return encodeHex(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
    }

    private fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
        val l = data.size
        val out = CharArray(l shl 1)
        var i = 0
        var var5 = 0
        while (i < l) {
            out[var5++] = toDigits[240 and data[i].toInt() ushr 4]
            out[var5++] = toDigits[15 and data[i].toInt()]
            ++i
        }
        return out
    }

}