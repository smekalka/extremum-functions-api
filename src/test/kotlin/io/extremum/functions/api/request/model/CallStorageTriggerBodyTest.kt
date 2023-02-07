package io.extremum.functions.api.request.model

import io.extremum.functions.api.function.model.StorageTriggerParameters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Date

class CallStorageTriggerBodyTest {

    @Test
    fun toParameters() {
        val eventId = "eventId1"
        val eventType = "yandex.cloud.events.storage.ObjectCreate"
        val createdAt = Date()
        val bucketId = "bucketId2"
        val objectId = "objectId3"
        val message = CallStorageTriggerBody.Message(
            eventMetadata = EventMetadata(
                eventId = eventId,
                eventType = eventType,
                createdAt = createdAt,
            ),
            details = CallStorageTriggerBody.Message.Details(
                bucketId = bucketId,
                objectId = objectId,
            ),
        )
        val result = message.toParametersInstance()
        val exp = StorageTriggerParameters.Instance(
            id = eventId,
            created = createdAt,
            operation = "create",
            key = bucketId,
            meta = StorageTriggerParameters.Meta(
                trace = ""
            )
        )
        assertEquals(exp, result)
    }

    @Test
    fun getOperation() {
        assertEquals("delete", CallStorageTriggerBody.getOperation("yandex.cloud.events.storage.ObjectDelete"))
        assertEquals("create", CallStorageTriggerBody.getOperation("yandex.cloud.events.storage.ObjectCreate"))
    }
}