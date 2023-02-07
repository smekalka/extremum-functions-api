package io.extremum.functions.api.triggerTable

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TriggerTableParserTest {

    @Test
    fun parseTableString() {
        val result = TriggerTableParser.parseTriggerTable(
            """
c9266bad-b873-457b-9b78-d7a8cf84a398subc9266bad-b873-457b-9b78-d7a8cf84a398: []
storage.extr-1234-0-testtrigger.create: []
storage.extr-ajev84ud35k4m2lo4n39-0-autotest.create:
    - log-operations
    - log-operations2
    - package1.log-operations3
    - package2.log-operations4
storage.extr-ajev84ud35k4m2lo4n39-0-example111.create: []
storage.extr-ajev84ud35k4m2lo4n39-0-testtrigger.create:
    - functionbp
    - package1.log-operations3
    - package1.log-operations5
"""
        )
        assertThat(result).isEqualTo(
            mapOf(
                "c9266bad-b873-457b-9b78-d7a8cf84a398subc9266bad-b873-457b-9b78-d7a8cf84a398" to listOf(),
                "storage.extr-1234-0-testtrigger.create" to listOf(),
                "storage.extr-ajev84ud35k4m2lo4n39-0-autotest.create" to listOf(
                    TriggerTableParser.FunctionInfo("log-operations"),
                    TriggerTableParser.FunctionInfo("log-operations2"),
                    TriggerTableParser.FunctionInfo("log-operations3", packageName = "package1"),
                    TriggerTableParser.FunctionInfo("log-operations4", packageName = "package2"),
                ),
                "storage.extr-ajev84ud35k4m2lo4n39-0-example111.create" to listOf(),
                "storage.extr-ajev84ud35k4m2lo4n39-0-testtrigger.create" to listOf(
                    TriggerTableParser.FunctionInfo("functionbp"),
                    TriggerTableParser.FunctionInfo("log-operations3", packageName = "package1"),
                    TriggerTableParser.FunctionInfo("log-operations5", packageName = "package1"),
                ),
            )
        )
    }
}