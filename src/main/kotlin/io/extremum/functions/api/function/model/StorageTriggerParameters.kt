package io.extremum.functions.api.function.model

import java.util.Date

data class StorageTriggerParameters(
    val items: List<Instance>
) {
    data class Instance(
        val id: String,
        val type: String,
        val created: Date,
        val key: String,
        val meta: Meta,
    )

    data class Meta(
        val trace: String,
    )
}
