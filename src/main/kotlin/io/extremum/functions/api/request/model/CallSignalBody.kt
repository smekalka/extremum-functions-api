package io.extremum.functions.api.request.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.model.tools.mapper.MapperUtils.convertValueSafe
import java.util.Date
import java.util.logging.Logger

data class CallSignalBody(
    val messages: List<Message>
) {
    data class Message(
        @JsonProperty("event_metadata")
        val eventMetadata: EventMetadata,
        val details: Details
    ) {
        private val logger = Logger.getLogger(this::class.qualifiedName)

        fun toParametersSignal(): SignalParameters.Signal? {
            val body = details.message.body
            val map = body.convertValueSafe<Map<String, Any?>?>()
            val exchange = map?.get("exchange") ?: kotlin.run {
                logger.info("Ignoring signal event with as id ${eventMetadata.eventId} as event without signal. " +
                        "Message body doesn't have a field 'exchange': $body")
                return null
            }
            return SignalParameters.Signal(
                id = eventMetadata.eventId,
                created = Date(details.message.attributes.sentTimestamp),
                exchange = exchange.toString(),
                data = SignalParameters.Data(message = body),
                meta = SignalParameters.Meta(
                    trace = eventMetadata.tracingContext?.traceId ?: ""
                ),
            )
        }

        data class Details(
            @JsonProperty("queue_id")
            val queueId: String,
            val message: Message,
        ) {
            data class Message(
                @JsonProperty("message_id")
                val messageId: String,
                @JsonProperty("md5_of_body")
                val md5OfBody: String,
                val body: Any,
                val attributes: Attributes
            ) {
                data class Attributes(
                    @JsonProperty("SentTimestamp")
                    val sentTimestamp: Long
                )
            }
        }
    }
}