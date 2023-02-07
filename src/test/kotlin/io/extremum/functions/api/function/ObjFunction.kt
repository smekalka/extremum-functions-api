package io.extremum.functions.api.function

import io.extremum.common.annotation.function.Function
import io.extremum.common.annotation.function.FunctionContext
import io.extremum.common.annotation.function.FunctionMethod
import io.extremum.functions.api.function.model.Context
import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.functions.api.function.model.StorageTriggerParameters

@Function(name = "obj-f")
class ObjFunction : BasePackageFunction {

    @FunctionContext
    val context: Context = Context.EMPTY

    fun otherMethod() {
        println("launchedOtherMethod in objFunction")
    }

    @FunctionMethod
    fun launch(params: ParamsBody): String {
        println("launched objFunction with params: $params")
        return params.nested.subName
    }

    override suspend fun onStorageTrigger(parameters: StorageTriggerParameters) {
        println("\nlaunched ${this.javaClass.simpleName}.onStorageTrigger\n\n    with params: $parameters" +
                "\n\n    and context: $context")
    }

    override suspend fun onSignal(parameters: SignalParameters) {
        println("\nlaunched ${this.javaClass.simpleName}.onSignal\n\n    with params: $parameters" +
                "\n\n    and context: $context")
    }

    data class ParamsBody(
        val paramName: String,
        val size: Int = 1,
        val nested: NestedParams,
    )

    data class NestedParams (
        val subName: String
    )
}


