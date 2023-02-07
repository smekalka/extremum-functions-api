package io.extremum.functions.api.triggertable

internal object TriggerTableParser {

    /**
     * Находит в тексте строки вида
     * triggerId: []
     * и
     * triggerId:
     *     - functionInfo
     *     - ...
     * Где triggerId может содержать буквы, "." и "-",
     * functionInfo состоит из любых символов кроме ":" и идет после "    -".
     * Например, из текста
    storage.extr-1234-0-testtrigger.create: []
    storage.extr-ajev84ud35k4m2lo4n39-0-autotest.create:
    - log-operations
    - log-operations2
    - package1.log-operations3
     * Найдутся пары "storage.extr-1234-0-testtrigger.create" с " []"
     * и "storage.extr-ajev84ud35k4m2lo4n39-0-autotest.create" с
    - log-operations
    - log-operations2
    - package1.log-operations3
     */
    private val triggerWithFunctionsRegex = "([\\w\\.-]*):( \\[\\]|(?:\\n {4}.*)*)".toRegex()

    fun parseTriggerTable(triggerTableStr: String): Map<String, List<FunctionInfo>> {
        return triggerWithFunctionsRegex.findAll(triggerTableStr).toList().associate { matchResult ->
            val values = matchResult.groupValues
            val functionsStr = values[2]
            val functions = if (functionsStr == " []") {
                listOf()
            } else {
                parseFunctions(functionsStr)
            }
            values[1] to functions
        }
    }

    /**
     * Из заданного текста выбирает информацию о функции. Она следует после "    - " с новой строки.
     * Например, из
    - log-operations
    - log-operations2
    - package1.log-operations3
     * найдет 3 совпадения: log-operations, log-operations2, package1.log-operations3
     */
    private val functionRegex = "\\n {4}- (.*)".toRegex()

    private fun parseFunctions(functionsStr: String): List<FunctionInfo> {
        return functionRegex.findAll(functionsStr).toList().map {
            val functionInfo = it.groupValues[1]
            val split = functionInfo.split(".")
            if (split.size == 2) {
                FunctionInfo(functionName = split[1], packageName = split[0])
            } else {
                FunctionInfo(functionName = functionInfo)
            }
        }
    }

    data class FunctionInfo(
        val functionName: String,
        val packageName: String? = null,
    )
}