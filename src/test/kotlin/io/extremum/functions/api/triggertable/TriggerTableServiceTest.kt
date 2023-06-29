package io.extremum.functions.api.triggertable

import io.extremum.functions.api.function.FunctionsService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class TriggerTableServiceTest {

    @InjectMocks
    private lateinit var triggerTableService: TriggerTableService

    @Mock(lenient = true)
    private lateinit var functionsService: FunctionsService

    @Mock(lenient = true)
    private lateinit var triggerTableFetcher: TriggerTableFetcher

    @Test
    fun getTriggerTable() {
        runBlocking {
            whenever(functionsService.getPackageName()).thenReturn("packageNameValue")
            whenever(triggerTableFetcher.getTriggerTableMap()).thenReturn(
                mapOf(
                    "c9266bad-b873-457b-9b78-d7a8cf84a398subc9266bad-b873-457b-9b78-d7a8cf84a398" to listOf(),
                    "storage.extr-1234-0-testtrigger.create" to listOf(),
                    "storage.extr-1234-0-testtrigger.delete" to listOf("packageNameValue.log-operations3"),
                    "storage.extr-ajev84ud35k4m2lo4n39-0-autotest.create" to listOf(
                        "log-operations0",
                        "packageNameValue.log-operations1",
                        "packageNameValue.log-operations2",
                        "otherPackageName.log-operations4",
                    ),
                    "storage.extr-ajev84ud35k4m2lo4n39-0-example111.create" to listOf(),
                    "storage.extr-ajev84ud35k4m2lo4n39-0-example111.create" to listOf("functionbp"),
                )
            )
            val result = triggerTableService.getTriggerTable()

            val exp = mapOf(
                "storage.extr-1234-0-testtrigger.delete" to listOf("log-operations3"),
                "storage.extr-ajev84ud35k4m2lo4n39-0-autotest.create" to listOf("log-operations1", "log-operations2"),
            )
            assertEquals(exp, result)
        }
    }
}