package io.extremum.functions.api.triggertable

internal object TriggerTableParser {

    fun getFunctionInfo(functionInfo: String): FunctionInfo {
        val split = functionInfo.split(".")
        return if (split.size == 2) {
            FunctionInfo(functionName = split[1], packageName = split[0])
        } else {
            FunctionInfo(functionName = functionInfo)
        }
    }

    data class FunctionInfo(
        val functionName: String,
        val packageName: String? = null,
    )
}