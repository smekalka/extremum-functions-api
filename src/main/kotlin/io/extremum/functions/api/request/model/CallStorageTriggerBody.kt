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
            type = getType(eventMetadata.eventType),
            created = eventMetadata.createdAt,
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
        fun getType(rawEventType: String): String {
            val last = getLastPart(rawEventType)
            return "extremum.storage.$last"
        }

        fun getOperation(rawEventType: String): String {
            val last = getLastPart(rawEventType)
            return last.removePrefix("Object").lowercase()
        }

        private fun getLastPart(rawEventType: String): String {
            val split = rawEventType.split(".")
            return split.last()
        }
    }
}

