package io.extremum.functions.api.request

import io.extremum.functions.api.function.FunctionsService
import io.extremum.functions.api.function.model.Context
import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.functions.api.function.model.StorageTriggerParameters
import io.extremum.functions.api.request.model.CallFunctionBody
import io.extremum.functions.api.request.model.CallSignalBody
import io.extremum.functions.api.request.model.CallStorageTriggerBody
import io.extremum.functions.api.request.model.EventMetadata
import io.extremum.functions.api.triggertable.FunctionsOnSignalResolver
import io.extremum.functions.api.triggertable.FunctionsOnStorageTriggerResolver
import io.extremum.model.tools.mapper.MapperUtils.convertToMap
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

@ExtendWith(MockitoExtension::class)
class RequestRouterTest {

    @InjectMocks
    private lateinit var requestRouter: RequestRouter

    @Mock(lenient = true)
    private lateinit var functionsService: FunctionsService

    @Mock(lenient = true)
    private lateinit var functionsOnStorageTriggerResolver: FunctionsOnStorageTriggerResolver

    @Mock(lenient = true)
    private lateinit var functionsOnSignalResolver: FunctionsOnSignalResolver

    @Test
    fun callMethod() {
        runBlocking {
            val methodResponse = "method response"
            whenever(functionsService.callMethod(any(), any(), any(), any())).thenReturn(methodResponse)
            val context = Context(
                headers = mapOf("auth" to "1"),
                `package` = "p",
                function = "f",
            )
            val body = CallFunctionBody(
                parameters = "params value",
                context = context
            )

            val result = requestRouter.onRequest(body.convertToMap())
            assertEquals(methodResponse, result)

            verify(functionsService).callMethod(context.`package`, context.function, context, body.parameters)
        }
    }

    @Test
    fun onStorageTrigger() {
        runBlocking {
            whenever(functionsService.onStorageTrigger(any(), any())).then { }

            val message1 = callStorageTriggerMessage("1", eventType = "extremum.storage.ObjectDelete")
            val message2 = callStorageTriggerMessage("2", eventType = "yandex.cloud.events.storage.ObjectCreate")
            val message3 = callStorageTriggerMessage("3")
            val functionName1 = "f1"
            val functionName2 = "f2"
            mockFunctionsOnStorageTriggerResolver(message1, listOf(functionName1, functionName2))
            mockFunctionsOnStorageTriggerResolver(message2, listOf(functionName2))
            mockFunctionsOnStorageTriggerResolver(message3, listOf())
            val body = CallStorageTriggerBody(
                messages = listOf(
                    message1,
                    message2,
                    message3,
                )
            )

            requestRouter.onRequest(body.convertToMap())

            verify(functionsService).onStorageTrigger(
                functionName1, StorageTriggerParameters(
                    items = listOf(
                        message1.toParametersInstance(),
                    )
                )
            )
            verify(functionsService).onStorageTrigger(
                functionName2, StorageTriggerParameters(
                    items = listOf(
                        message1.toParametersInstance(),
                        message2.toParametersInstance(),
                    )
                )
            )
        }
    }

    private fun callStorageTriggerMessage(id: String, eventType: String = "yandex.cloud.events.storage.ObjectDelete"): CallStorageTriggerBody.Message =
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

    private suspend fun mockFunctionsOnStorageTriggerResolver(
        onMessage: CallStorageTriggerBody.Message,
        functionsToReturn: List<String>
    ) {
        whenever(functionsOnStorageTriggerResolver.get(onMessage.details.bucketId, onMessage.eventMetadata.eventType))
            .thenReturn(functionsToReturn)
    }

    @Test
    fun onSignal() {
        runBlocking {
            whenever(functionsService.onStorageTrigger(any(), any())).then { }

            val message1 = callSignalMessage("1")
            val message2 = callSignalMessage("2")
            val message3 = callSignalMessage("3")
            val functionName1 = "f1"
            val functionName2 = "f2"
            mockFunctionsOnSignalResolver(message1, listOf(functionName1, functionName2))
            mockFunctionsOnSignalResolver(message2, listOf(functionName2))
            mockFunctionsOnSignalResolver(message3, listOf())
            val body = CallSignalBody(
                messages = listOf(
                    message1,
                    message2,
                    message3,
                )
            )

            requestRouter.onRequest(body.convertToMap())

            verify(functionsService).onSignal(
                functionName1, SignalParameters(
                    items = listOf(
                        message1.toParametersSignal()!!,
                    )
                )
            )
            verify(functionsService).onSignal(
                functionName2, SignalParameters(
                    items = listOf(
                        message1.toParametersSignal()!!,
                        message2.toParametersSignal()!!,
                    )
                )
            )
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
                    body = Trigger("exchangeValue$id").convertToMap(),
                    attributes = CallSignalBody.Message.Details.Message.Attributes(Date().time),
                ),
            ),
        )

    private suspend fun mockFunctionsOnSignalResolver(
        onMessage: CallSignalBody.Message,
        functionsToReturn: List<String>
    ) {
        whenever(functionsOnSignalResolver.get(onMessage.details.queueId))
            .thenReturn(functionsToReturn)
    }
}