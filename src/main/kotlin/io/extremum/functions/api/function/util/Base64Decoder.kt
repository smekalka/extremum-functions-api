package io.extremum.functions.api.function.util

import java.util.Base64

object Base64Decoder {
    private val decoder = Base64.getDecoder()

    fun String.base64Decode(): String = String(decoder.decode(this))
}