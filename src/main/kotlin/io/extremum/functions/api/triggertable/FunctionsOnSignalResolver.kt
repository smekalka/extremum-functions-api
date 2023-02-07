package io.extremum.functions.api.triggertable

import org.springframework.stereotype.Service

@Service
internal class FunctionsOnSignalResolver(
    private val triggerTableService: TriggerTableService
) {
    suspend fun get(queueId: String): List<String> {
        val triggerId = getTriggerId(queueId)
        val triggerTable = triggerTableService.getTriggerTable()
        return triggerTable[triggerId] ?: listOf()
    }

    private companion object {
        fun getTriggerId(queueId: String): String = queueId.split(":").last()
    }
}