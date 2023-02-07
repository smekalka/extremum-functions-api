package io.extremum.functions.api.request.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.extremum.functions.api.function.model.StorageTriggerParameters

data class CallStorageTriggerBody(
    val messages: List<Message>
) {
    data class Message(
        @JsonProperty("event_metadata")
        val eventMetadata: EventMetadata,
        val details: Details
    ) {
        fun toParametersInstance(): StorageTriggerParameters.Instance = StorageTriggerParameters.Instance(
            id = eventMetadata.eventId,
            created = eventMetadata.createdAt,
            operation = getOperation(eventMetadata.eventType),
            key = details.bucketId,
            meta = StorageTriggerParameters.Meta(
                trace = eventMetadata.tracingContext?.traceId ?: ""
            )
        )

        data class Details(
            @JsonProperty("bucket_id")
            val bucketId: String,
            @JsonProperty("object_id")
            val objectId: String,
        )
    }

    companion object {
        fun getOperation(rawEventType: String): String {
            val split = rawEventType.split(".")
            return split[4].removePrefix("Object").lowercase()
        }
    }
}

