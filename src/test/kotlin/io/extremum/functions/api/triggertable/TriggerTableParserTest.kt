package io.extremum.functions.api.triggertable

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TriggerTableParserTest {

    @Test
    fun getFunctionInfo() {
        assertThat(TriggerTableParser.getFunctionInfo("pack.func"))
            .isEqualTo(TriggerTableParser.FunctionInfo(packageName = "pack", functionName = "func"))

        assertThat(TriggerTableParser.getFunctionInfo("function-name"))
            .isEqualTo(TriggerTableParser.FunctionInfo(functionName = "function-name"))
    }
}