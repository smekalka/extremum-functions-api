package io.extremum.functions.api.function.model

import java.util.Date

data class StorageTriggerParameters(
    val items: List<Instance>
) {
    data class Instance(
        val id: String,
        val type: String = DEFAULT_TYPE,
        val created: Date,
        val operation: String,
        val key: String,
        val meta: Meta,
    )

    data class Meta(
        val trace: String,
    )

    private companion object {
        const val DEFAULT_TYPE = "StorageEvent"
    }
}
