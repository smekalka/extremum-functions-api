package io.extremum.functions.api.function

import io.extremum.common.annotation.function.Function
import io.extremum.common.annotation.function.FunctionMethod
import io.extremum.functions.api.function.model.Context
import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.functions.api.function.model.StorageTriggerParameters

@Function(name = "map-f")
class MapFunction : BasePackageFunction {

    var value : Int = 0

    fun otherMethod() {
        println("launchedOtherMethod")
    }

    @FunctionMethod
    fun launch(context: Context, params: Map<String, Any?>): Map<String, Any?> {
        println("launched ${this.javaClass.simpleName} with params: $params\n    and context: $context")
        return params + ("value" to value) + context.headers
    }

    override suspend fun onStorageTrigger(context: Context, parameters: StorageTriggerParameters) {
        println("\nlaunched ${this.javaClass.simpleName}.onStorageTrigger\n\n    with params: $parameters" +
                "\n\n    and context: $context")
    }

    override suspend fun onSignal(context: Context, parameters: SignalParameters) {
        println("\nlaunched ${this.javaClass.simpleName}.onSignal\n\n    with params: $parameters" +
                "\n\n    and context: $context")
    }
}