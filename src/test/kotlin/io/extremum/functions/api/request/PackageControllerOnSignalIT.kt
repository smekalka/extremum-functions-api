package io.extremum.functions.api.request

import io.extremum.functions.api.keycloak.CredentialService
import io.extremum.functions.api.request.model.CallSignalBody
import io.extremum.functions.api.request.model.EventMetadata
import io.extremum.functions.api.triggertable.TriggerTableService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.util.ReflectionTestUtils
import java.util.Date

@SpringBootTest
class PackageControllerOnSignalIT {

    @Autowired
    private lateinit var packageController: PackageController

    @Autowired
    private lateinit var triggerTableService: TriggerTableService

    @Autowired
    private lateinit var credentialService: CredentialService

    @Test
    fun onSignal() {
        runBlocking {
            val testTriggerTable = mapOf(
                "queues.queueIdValue1" to listOf("list-f"),
                "queues.queueIdValue2" to listOf("map-f"),
            )
            ReflectionTestUtils.setField(triggerTableService, "triggerTable", testTriggerTable)
            ReflectionTestUtils.setField(credentialService, "headers", mapOf("auth" to "auth_jwt_token"))
            val message1 = callSignalMessage("1")
            val message2 = callSignalMessage("2")
            val message3 = callSignalMessage("3")
            val body = CallSignalBody(
                messages = listOf(
                    message1,
                    message2,
                    message3,
                )
            )

            val result = packageController.call(body)

            assertEquals(HttpStatus.OK.value(), result.code)
        }
    }

    private data class Trigger(
        val exchange: String
    )

    private fun callSignalMessage(id: String): CallSignalBody.Message =
        CallSignalBody.Message(
            eventMetadata = EventMetadata(
                eventId = "eventIdValue$id",
                eventType = "yandex.cloud.events.messagequeue.QueueMessage",
                createdAt = Date(),
                tracingContext = EventMetadata.TracingContext(traceId = "traceIdValue$id"),
            ),
            details = CallSignalBody.Message.Details(
                queueId = "queueIdValue$id",
                message = CallSignalBody.Message.Details.Message(
                    messageId = "messageIdValue$id",
                    md5OfBody = "md5OfBodyValue$id",
                    body = Trigger("exchangeValue$id"),
                    attributes = CallSignalBody.Message.Details.Message.Attributes(Date().time),
                ),
            ),
        )
}