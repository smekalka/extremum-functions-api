package io.extremum.functions.api.request.model

import com.fasterxml.jackson.annotation.JsonProperty

data class CallTriggerBody(
    val messages: List<Message>
) {
    data class Message(
        @JsonProperty("event_metadata")
        val eventMetadata: EventMetadata,
    )
}