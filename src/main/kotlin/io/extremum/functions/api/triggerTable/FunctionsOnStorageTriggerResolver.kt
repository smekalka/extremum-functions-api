package io.extremum.functions.api.triggerTable

import io.extremum.functions.api.request.model.CallStorageTriggerBody
import org.springframework.stereotype.Service

@Service
internal class FunctionsOnStorageTriggerResolver(
    private val triggerTableService: TriggerTableService
) {
    suspend fun get(bucketId: String, rawEventType: String): List<String> {
        val triggerId = getTriggerId(bucketId, CallStorageTriggerBody.getOperation(rawEventType))
        return triggerTableService.getTriggerTable()[triggerId] ?: listOf()
    }

    private companion object {
        fun getTriggerId(bucketId: String, eventType: String): String = "storage.$bucketId.$eventType"
    }
}