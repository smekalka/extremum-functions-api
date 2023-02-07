package io.extremum.functions.api.function.model

import java.util.Date

data class SignalParameters(
    val items: List<Signal>
) {
    data class Signal(
        val id: String,
        val type: String = DEFAULT_TYPE,
        val created: Date,
        val kind: String = DEFAULT_KIND,
        val exchange: String,
        val data: Data,
        val meta: Meta,
    )

    data class Data(
        val message: Any,
    )

    data class Meta(
        val trace: String,
    )

    private companion object {
        const val DEFAULT_TYPE = "Signal"
        const val DEFAULT_KIND = "regular"
    }
}