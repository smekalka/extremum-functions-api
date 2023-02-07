package io.extremum.functions.api.request.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class EventMetadata(
    @JsonProperty("event_id")
    val eventId: String,
    @JsonProperty("event_type")
    val eventType: String,
    @JsonProperty("created_at")
    val createdAt: Date,
    @JsonProperty("tracing_context")
    val tracingContext: TracingContext? = null,
) {
    data class TracingContext(
        @JsonProperty("trace_id")
        val traceId: String,
    )
}