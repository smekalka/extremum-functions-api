package io.extremum.functions.api.triggertable

import io.extremum.functions.api.function.FunctionsService
import io.extremum.functions.api.triggertable.TriggerTableParser.getFunctionInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TriggerTableService {

    @Autowired
    private lateinit var functionsService: FunctionsService

    @Autowired
    private lateinit var  triggerTableFetcher: TriggerTableFetcher

    private val packageName: String
            by lazy { functionsService.getPackageName() }

    /**
     * Id триггера к названиям функций этого пакета
     */
    private var triggerTable: Map<String, List<String>>? = null

    suspend fun getTriggerTable(): Map<String, List<String>> {
        triggerTable?.run { return this }

        val triggerTableMap = triggerTableFetcher.getTriggerTableMap()
        val filtered = triggerTableMap.mapNotNull { (triggerId, functions) ->
            val filteredFunctions = functions.mapNotNull {
                val info = getFunctionInfo(it)
                if (info.packageName == packageName) info.functionName else null
            }
            if (filteredFunctions.isEmpty()) {
                null
            } else {
                triggerId to filteredFunctions
            }
        }.toMap()
        triggerTable = filtered
        return filtered
    }
}