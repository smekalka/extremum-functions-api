package io.extremum.functions.api.request.model

import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.model.tools.mapper.MapperUtils.convertToMap
import io.extremum.test.tools.ToJsonFormatter.toJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.Date

class CallSignalBodyTest {

    @Test
    fun toParameters() {
        data class Signal(
            val exchange: String
        )
        val body = Signal(exchange)
        val result = message(body).toParametersSignal()
        assertEquals(signal(body), result)
    }

    @Test
    fun `toParameters for map`() {
        data class Signal(
            val exchange: String
        )
        val body = Signal(exchange).convertToMap()
        val result = message(body).toParametersSignal()
        assertEquals(signal(body), result)
    }

    @Test
    fun `toParameters for string`() {
        data class Signal(
            val exchange: String
        )
        val body = Signal(exchange).toJson()
        val result = message(body).toParametersSignal()
        assertEquals(signal(body), result)
    }

    @Test
    fun `toParameters without exchange in body`() {
        data class MessageBody(
            val data: String
        )
        val body = MessageBody("bodyValue1")
        val result = message(body).toParametersSignal()
        assertNull(result)
    }

    @Test
    fun `toParameters with string in body`() {
        val result = message("bodyValue1").toParametersSignal()
        assertNull(result)
    }

    private companion object {
        const val eventId = "eventId1"
        const val eventType = "yandex.cloud.events.messagequeue.QueueMessage"
        val createdAt = Date()
        val messageCreatedAt = Date()
        const val queueId = "queueIdValue1"
        const val exchange = "exchangeValue1"
        const val traceId = "traceIdValue1"

        fun signal(body: Any)= SignalParameters.Signal(
            id = eventId,
            created = messageCreatedAt,
            exchange = exchange,
            data = SignalParameters.Data(message = body),
            meta = SignalParameters.Meta(
                trace = traceId
            ),
        )

        fun message(body: Any) = CallSignalBody.Message(
            eventMetadata = EventMetadata(
                eventId = eventId,
                eventType = eventType,
                createdAt = createdAt,
                tracingContext = EventMetadata.TracingContext(traceId = traceId),
            ),
            details = CallSignalBody.Message.Details(
                queueId = queueId,
                message = CallSignalBody.Message.Details.Message(
                    messageId = "messageIdValue1",
                    md5OfBody = "md5OfBodyValue1",
                    body = body,
                    attributes = CallSignalBody.Message.Details.Message.Attributes(messageCreatedAt.time),
                ),
            ),
        )
    }
}