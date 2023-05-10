package io.extremum.functions.api.request

import io.extremum.functions.api.exception.ArgumentValidationException
import io.extremum.functions.api.function.FunctionsService
import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.functions.api.function.model.StorageTriggerParameters
import io.extremum.functions.api.request.model.CallFunctionBody
import io.extremum.functions.api.request.model.CallSignalBody
import io.extremum.functions.api.request.model.CallStorageTriggerBody
import io.extremum.functions.api.request.model.CallTriggerBody
import io.extremum.functions.api.triggertable.FunctionsOnSignalResolver
import io.extremum.functions.api.triggertable.FunctionsOnStorageTriggerResolver
import io.extremum.model.tools.mapper.MapperUtils.convertToMap
import io.extremum.model.tools.mapper.MapperUtils.convertValue
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
internal class RequestRouter(
    private val functionsService: FunctionsService,
    private val functionsOnStorageTriggerResolver: FunctionsOnStorageTriggerResolver,
    private val functionsOnSignalResolver: FunctionsOnSignalResolver,
) {
    private val logger = Logger.getLogger(this::class.qualifiedName)

    suspend fun onRequest(body: Any): Any? {
        if (body.convertToMap()["messages"] == null) {
            return callFunctionMethod(body)
        }

        val isStorageBody = isStorageBody(body)
        if (isStorageBody) {
            onStorageTrigger(body)
            return null
        }

        onSignal(body)
        return null
    }

    private suspend fun callFunctionMethod(body: Any): Any? {
        logger.info("callFunctionMethod for $body")
        val callFunctionBody = body.convertValueWithLog<CallFunctionBody>()
        return with(callFunctionBody) {
            functionsService.callMethod(context.`package`, context.function, context, parameters)
        }
    }

    private suspend fun onStorageTrigger(body: Any) {
        logger.info("onStorageTrigger for $body")
        val storageBody = body.convertValueWithLog<CallStorageTriggerBody>()
        val messageToFunctionList = storageBody.messages.map { message ->
            functionsOnStorageTriggerResolver.get(
                bucketId = message.details.bucketId,
                rawEventType = message.eventMetadata.eventType,
            ).map { functionName -> message.toParametersInstance() to functionName }
        }.flatten()
        val functionToInstancesMap = messageToFunctionList
            .groupBy { it.second }
            .mapValues { it.value.map { messageToFunction -> messageToFunction.first } }
        logger.info(
            "On storage trigger for events ${storageBody.messages.map { it.eventMetadata.eventId }}\n  " +
                    "found functions\n    ${
                        functionToInstancesMap.map { (key, value) ->
                            "$key: ${value.joinToString { it.id }}"
                        }.joinToString("\n    ")
                    }"
        )
        functionToInstancesMap.forEach { (functionName, instances) ->
            functionsService.onStorageTrigger(functionName, StorageTriggerParameters(items = instances))
        }
    }

    private suspend fun onSignal(body: Any) {
        logger.info("onSignal for $body")
        val signalBody = body.convertValueWithLog<CallSignalBody>()
        val messageToFunctionList = signalBody.messages.map { message ->
            functionsOnSignalResolver.get(queueId = message.details.queueId)
                .mapNotNull { functionName ->
                    message.toParametersSignal()?.let { it to functionName }
                }
        }.flatten()
        val functionToInstancesMap = messageToFunctionList
            .groupBy { it.second }
            .mapValues { it.value.map { messageToFunction -> messageToFunction.first } }
        logger.info(
            "On signal for events ${signalBody.messages.map { it.eventMetadata.eventId }}\n  " +
                    "found functions\n    ${
                        functionToInstancesMap.map { (key, value) ->
                            "$key: ${value.joinToString { it.id }}"
                        }.joinToString("\n    ")
                    }"
        )
        functionToInstancesMap.forEach { (functionName, instances) ->
            functionsService.onSignal(functionName, SignalParameters(items = instances))
        }
    }

    private inline fun <reified R> Any.convertValueWithLog(): R = try {
        this.convertValue()
    } catch (e: IllegalArgumentException) {
        val argumentValidationException = ArgumentValidationException("Can't convert to ${R::class.java.simpleName} $this:\n${e.message}")
        logger.severe(argumentValidationException.message)
        throw argumentValidationException
    }

    private fun isStorageBody(body: Any): Boolean {
        val triggerBody = body.convertValueWithLog<CallTriggerBody>()
        val rawEventType = triggerBody.messages.first().eventMetadata.eventType
        val split = rawEventType.split(".")
        return split.contains(EVENT_TYPE_STORAGE_MARK)
    }

    private companion object {
        const val EVENT_TYPE_STORAGE_MARK = "storage"
    }
}