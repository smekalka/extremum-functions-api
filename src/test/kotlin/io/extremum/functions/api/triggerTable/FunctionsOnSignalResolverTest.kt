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
class FunctionsOnSignalResolverTest {

    @InjectMocks
    private lateinit var functionsOnSignalResolver: FunctionsOnSignalResolver

    @Mock(lenient = true)
    private lateinit var triggerTableService: TriggerTableService

    @Test
    fun get() {
        runBlocking {
            val triggerId = "2a505015-92e2-4e66-8b1e-06c8b0b331a9sub2a505015-92e2-4e66-8b1e-06c8b0b331a9"
            val queueId = "yrn:yc:ymq:ru-central1:b1gqo0vnv4k9i8oh9en6:$triggerId"
            val functions = listOf("f3", "f4")
            whenever(triggerTableService.getTriggerTable()).thenReturn(
                mapOf(
                    "tr1" to listOf("f1", "f2"),
                    triggerId to functions,
                )
            )

            assertEquals(functions, functionsOnSignalResolver.get(queueId))

            assertEquals(listOf<String>(), functionsOnSignalResolver.get("q3"))
        }
    }
}