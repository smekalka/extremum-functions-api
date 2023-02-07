package io.extremum.functions.api.request

import io.extremum.functions.api.keycloak.CredentialService
import io.extremum.functions.api.request.model.CallStorageTriggerBody
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
class PackageControllerOnStorageTriggerIT {

    @Autowired
    private lateinit var packageController: PackageController

    @Autowired
    private lateinit var triggerTableService: TriggerTableService

    @Autowired
    private lateinit var credentialService: CredentialService

    @Test
    fun onStorageTrigger() {
        runBlocking {
            val testTriggerTable = mapOf(
                "storage.bucketIdValue1.delete" to listOf("list-f"),
                "storage.bucketIdValue2.create" to listOf("map-f"),
            )
            ReflectionTestUtils.setField(triggerTableService, "triggerTable", testTriggerTable)
            ReflectionTestUtils.setField(credentialService, "headers", mapOf("auth" to "auth_jwt_token"))
            val message1 = callStorageTriggerMessage("1", eventType = "yandex.cloud.events.storage.ObjectDelete")
            val message2 = callStorageTriggerMessage("2", eventType = "yandex.cloud.events.storage.ObjectCreate")
            val message3 = callStorageTriggerMessage("3", eventType = "yandex.cloud.events.storage.ObjectDelete")
            val body = CallStorageTriggerBody(
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

    private fun callStorageTriggerMessage(id: String, eventType: String) =
        CallStorageTriggerBody.Message(
            eventMetadata = EventMetadata(
                eventId = "eventIdValue$id",
                eventType = eventType,
                createdAt = Date(),
                tracingContext = EventMetadata.TracingContext(traceId = "traceIdValue$id"),
            ),
            details = CallStorageTriggerBody.Message.Details(
                bucketId = "bucketIdValue$id",
                objectId = "objectIdValue$id",
            ),
        )
}