package io.extremum.functions.api.triggerTable

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class FunctionsOnStorageTriggerResolverTest {

    @InjectMocks
    private lateinit var functionsOnStorageTriggerResolver: FunctionsOnStorageTriggerResolver

    @Mock(lenient = true)
    private lateinit var triggerTableService: TriggerTableService

    @Test
    fun get() {
        runBlocking {
            val functions = listOf("f3", "f4")
            val bucketId = "s22"
            val eventType = "yandex.cloud.events.storage.ObjectDelete"
            whenever(triggerTableService.getTriggerTable()).thenReturn(
                mapOf(
                    "tr1" to listOf("f1", "f2"),
                    "storage.s22.delete" to functions,
                )
            )

            assertEquals(functions, functionsOnStorageTriggerResolver.get(bucketId, eventType))

            assertEquals(listOf<String>(), functionsOnStorageTriggerResolver.get("1", eventType))
        }
    }
}